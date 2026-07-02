package be.kdg.prog5.hotels.business.ai;

import be.kdg.prog5.hotels.business.customer.CustomerService;
import be.kdg.prog5.hotels.business.exceptions.BookingException;
import be.kdg.prog5.hotels.business.exceptions.BookingExceptionMessageResolver;
import be.kdg.prog5.hotels.business.exceptions.RoomNotFoundException;
import be.kdg.prog5.hotels.business.security.SecurityService;
import be.kdg.prog5.hotels.data.SpringDataRoomRepository;
import be.kdg.prog5.hotels.domain.Customer;
import be.kdg.prog5.hotels.domain.Room;
import be.kdg.prog5.hotels.domain.Stay;
import be.kdg.prog5.hotels.webapi.dto.ai.AiBookingActionResponseDto;
import be.kdg.prog5.hotels.webapi.dto.ai.AiBookingCancelRequestDto;
import be.kdg.prog5.hotels.webapi.dto.ai.AiBookingConfirmRequestDto;
import be.kdg.prog5.hotels.webapi.dto.ai.AiBookingQuoteRequestDto;
import be.kdg.prog5.hotels.webapi.dto.ai.AiBookingQuoteResponseDto;
import be.kdg.prog5.hotels.webapi.dto.ai.AiBookingSummaryDto;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional
public class AiBookingServiceImpl implements AiBookingService {
    // Repository is used directly here because the AI flow resolves rooms by id or visible room number
    private final SpringDataRoomRepository roomRepository;

    // CustomerService owns the real booking and cancellation business rules
    private final CustomerService customerService;

    // SecurityService keeps the chatbot tied to the current authenticated customer
    private final SecurityService securityService;

    public AiBookingServiceImpl(SpringDataRoomRepository roomRepository,
                                CustomerService customerService,
                                SecurityService securityService) {
        this.roomRepository = roomRepository;
        this.customerService = customerService;
        this.securityService = securityService;
    }

    @Override
    @Transactional(readOnly = true)
    public AiBookingQuoteResponseDto quote(AiBookingQuoteRequestDto request) {
        requireCustomer();
        validateDates(request.checkIn(), request.checkOut());

        // A quote is read-only, but it must use the same room lookup rules as confirmation
        Room room = resolveRoom(request.roomId(), request.roomNumber());
        Customer customer = currentCustomer();
        long nights = ChronoUnit.DAYS.between(request.checkIn(), request.checkOut());
        BigDecimal totalPrice = room.getPricePerNight().multiply(BigDecimal.valueOf(nights));

        // Discount is applied here so the chatbot can show the customer the exact final price
        BigDecimal discountPercentage = customer.getProfile().getDiscountPercentage() == null
                ? BigDecimal.ZERO
                : customer.getProfile().getDiscountPercentage();
        BigDecimal discountAmount = totalPrice.multiply(discountPercentage).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal finalPrice = totalPrice.subtract(discountAmount).setScale(2, RoundingMode.HALF_UP);
        boolean available = room.isAvailable(request.checkIn(), request.checkOut());
        String message = available
                ? "Room is available. Please confirm if you want to book it."
                : "Room is not available for those dates. Please choose another date or room.";

        return new AiBookingQuoteResponseDto(
                available,
                message,
                room.getId(),
                room.getNumber(),
                room.getHotel().getName(),
                room.getHotel().getCity(),
                room.getType().name(),
                request.checkIn(),
                request.checkOut(),
                nights,
                room.getPricePerNight(),
                totalPrice.setScale(2, RoundingMode.HALF_UP),
                discountPercentage,
                finalPrice
        );
    }

    @Override
    public AiBookingActionResponseDto confirm(AiBookingConfirmRequestDto request) {
        requireCustomer();

        try {
            // Confirmation delegates to the normal customer booking path to avoid duplicate rules
            validateDates(request.checkIn(), request.checkOut());
            customerService.bookOwnRoom(securityService.getLoggedInUsername(), request.roomId(), request.checkIn(), request.checkOut());
            return new AiBookingActionResponseDto(true, "Booking confirmed. You can view it in My bookings.");
        } catch (BookingException exception) {
            return new AiBookingActionResponseDto(false, BookingExceptionMessageResolver.toMessage(exception.getCode()));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<AiBookingSummaryDto> currentCustomerBookings() {
        requireCustomer();
        String email = securityService.getLoggedInUsername();

        // Sorted bookings make the cancel list predictable inside the chat window
        return customerService.getBookings(email)
                .stream()
                .sorted(Comparator.comparing(Stay::getCheckInDate))
                .map(this::toSummary)
                .toList();
    }

    @Override
    public AiBookingActionResponseDto cancel(AiBookingCancelRequestDto request) {
        requireCustomer();

        // Service method verifies ownership before deleting the stay
        customerService.cancelOwnBooking(securityService.getLoggedInUsername(), request.stayId());
        return new AiBookingActionResponseDto(true, "Booking cancelled successfully.");
    }

    private void requireCustomer() {
        // AI booking endpoints are stricter than search because they mutate customer stays
        if (!securityService.isCustomer()) {
            throw new AccessDeniedException("Please login as a customer first, then come back to the bot.");
        }
    }

    private Customer currentCustomer() {
        return customerService.getCustomerByEmail(securityService.getLoggedInUsername());
    }

    private void validateDates(LocalDate checkIn, LocalDate checkOut) {
        // Keep validation close to quote and confirm so both paths reject the same bad dates
        if (checkIn == null || checkOut == null) {
            throw new IllegalArgumentException("Please provide both check-in and check-out dates.");
        }

        if (!checkOut.isAfter(checkIn)) {
            throw new IllegalArgumentException("Check-out must be after check-in.");
        }

        if (checkIn.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Check-in cannot be in the past.");
        }
    }

    private Room resolveRoom(Long roomId, Integer roomNumber) {
        if (roomId != null) {
            // Room cards send a stable id, which is preferred when available
            return roomRepository.findByIdWithHotelAndGuests(roomId)
                    .orElseThrow(() -> new RoomNotFoundException(roomId));
        }

        if (roomNumber == null) {
            throw new IllegalArgumentException("Please choose a room first.");
        }

        // Typed room numbers must be unique before the assistant can safely book them
        List<Room> rooms = roomRepository.findByNumberWithHotel(roomNumber);
        if (rooms.isEmpty()) {
            throw new IllegalArgumentException("I could not find room " + roomNumber + ".");
        }

        if (rooms.size() > 1) {
            throw new IllegalArgumentException("I found more than one room with that number. Please click Book on the exact room card.");
        }

        Long resolvedRoomId = rooms.getFirst().getId();
        return roomRepository.findByIdWithHotelAndGuests(resolvedRoomId)
                .orElseThrow(() -> new RoomNotFoundException(resolvedRoomId));
    }

    private AiBookingSummaryDto toSummary(Stay stay) {
        return new AiBookingSummaryDto(
                stay.getId(),
                stay.getRoom().getId(),
                stay.getRoom().getNumber(),
                stay.getRoom().getHotel().getName(),
                stay.getRoom().getHotel().getCity(),
                stay.getCheckInDate(),
                stay.getCheckOutDate(),
                stay.getFinalPrice()
        );
    }

}
