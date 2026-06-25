package be.kdg.prog5.hotels.business.booking;

import be.kdg.prog5.hotels.business.activity.SafeActivityLogger;
import be.kdg.prog5.hotels.business.exceptions.BookingNotFoundException;
import be.kdg.prog5.hotels.business.exceptions.GuestNotFoundException;
import be.kdg.prog5.hotels.business.exceptions.RoomNotFoundException;
import be.kdg.prog5.hotels.data.SpringDataCustomerRepository;
import be.kdg.prog5.hotels.data.SpringDataGuestRepository;
import be.kdg.prog5.hotels.data.SpringDataRoomRepository;
import be.kdg.prog5.hotels.data.SpringDataStayRepository;
import be.kdg.prog5.hotels.domain.ActivityType;
import be.kdg.prog5.hotels.domain.Customer;
import be.kdg.prog5.hotels.domain.Guest;
import be.kdg.prog5.hotels.domain.Room;
import be.kdg.prog5.hotels.domain.Stay;
import be.kdg.prog5.hotels.business.security.SecurityService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;

@Service
@Transactional
public class BookingServiceImpl implements BookingService {

    private static final Logger log = LoggerFactory.getLogger(BookingServiceImpl.class);
    private static final int BOOKING_PAGE_SIZE = 10;

    private final SpringDataRoomRepository roomRepo;
    private final SpringDataGuestRepository guestRepo;
    private final SpringDataStayRepository stayRepo;
    private final SpringDataCustomerRepository customerRepo;
    private final SecurityService securityService;
    private final SafeActivityLogger safeActivityLogger;

    // Injects repositories needed to coordinate booking use cases
    public BookingServiceImpl(SpringDataRoomRepository roomRepo,
                              SpringDataGuestRepository guestRepo,
                              SpringDataStayRepository stayRepo,
                              SpringDataCustomerRepository customerRepo,
                              SecurityService securityService,
                              SafeActivityLogger safeActivityLogger) {
        this.roomRepo = roomRepo;
        this.guestRepo = guestRepo;
        this.stayRepo = stayRepo;
        this.customerRepo = customerRepo;
        this.securityService = securityService;
        this.safeActivityLogger = safeActivityLogger;
    }

    // Returns current and future bookings with guest, room, and hotel data already loaded
    @Override
    @Transactional(readOnly = true)
    public List<Stay> getCurrentBookings() {
        log.debug("Getting current bookings");
        return stayRepo.findCurrentBookingsWithDetails(LocalDate.now());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Stay> getCurrentBookings(int page, String search) {
        // Negative pages are treated as the first page
        int requestedPage = Math.max(page, 0);
        String normalizedSearch = normalizeSearch(search);
        Page<Stay> bookingPage = stayRepo.findCurrentBookingsWithDetails(
                LocalDate.now(),
                normalizedSearch,
                PageRequest.of(requestedPage, BOOKING_PAGE_SIZE)
        );

        // If the page is too high after filtering, return the last real page
        if (bookingPage.getTotalPages() > 0 && requestedPage >= bookingPage.getTotalPages()) {
            return stayRepo.findCurrentBookingsWithDetails(
                    LocalDate.now(),
                    normalizedSearch,
                    PageRequest.of(bookingPage.getTotalPages() - 1, BOOKING_PAGE_SIZE)
            );
        }

        return bookingPage;
    }

    @Override
    public String normalizeSearch(String search) {
        return search == null ? "" : search.trim();
    }

    @Override
    public List<Integer> getVisiblePageNumbers(Page<Stay> bookingPage) {
        // Shows at most five page numbers around the current page
        int lastPage = bookingPage.getTotalPages() - 1;
        int startPage = Math.max(0, bookingPage.getNumber() - 4);
        int endPage = Math.min(lastPage, startPage + 4);
        startPage = Math.max(0, endPage - 4);

        return IntStream.rangeClosed(startPage, endPage)
                .boxed()
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BookingFormDetails getBookingFormDetails(Long roomId) {
        // The form needs the room with hotel and existing stays already loaded
        Room room = roomRepo.findByIdWithHotelAndGuests(roomId)
                .orElseThrow(() -> new RoomNotFoundException(roomId));

        // Customers are only allowed to book for their own profile
        if (securityService.isCustomer()) {
            Customer customer = getLoggedInCustomer();
            return new BookingFormDetails(room, true, customer.getProfile(), List.of());
        }

        // Admin and staff can select any existing guest
        return new BookingFormDetails(room, false, null, guestRepo.findAll());
    }

    // Booking a room changes how many stays a guest has
    // Clear guest search cache because filters like minimum rooms depend on stay counts
    @Override
    // A new stay changes guest stay counts and room availability
    @CacheEvict(value = {"guestSearch", "roomSearch"}, allEntries = true)
    public void bookRoom(Long roomId, Long guestId,
                         LocalDate checkIn,
                         LocalDate checkOut) {
        log.debug("Booking room {} for guest {}", roomId, guestId);

        // Lock the room before checking availability so two requests cannot book it at the same time
        Room room = findRoomWithBookingLock(roomId);

        Guest guest = guestRepo.findById(guestId)
                .orElseThrow(() -> new GuestNotFoundException(guestId));

        room.addGuest(guest, checkIn, checkOut);

        safeActivityLogger.log(
                ActivityType.BOOK_ROOM,
                "Guest " + guest.getFullName() +
                        " booked room " + room.getNumber() +
                        " in hotel " + room.getHotel().getName()
        );
    }

    @Override
    public void bookRoomForCurrentUser(Long roomId, Long guestId, LocalDate checkIn, LocalDate checkOut) {
        // Ignore the submitted guest id for customers to prevent booking for someone else
        if (securityService.isCustomer()) {
            bookRoom(roomId, getLoggedInCustomer().getProfile().getId(), checkIn, checkOut);
            return;
        }

        bookRoom(roomId, guestId, checkIn, checkOut);
    }

    // Cancelling a booking also changes guest stay counts
    // Clear guest search cache so minimum room filters do not use stale results
    @Override
    // Removing a stay changes guest stay counts and room availability
    @CacheEvict(value = {"guestSearch", "roomSearch"}, allEntries = true)
    public void cancelBooking(Long stayId) {
        log.debug("Cancelling booking {}", stayId);

        Stay stay = stayRepo.findByIdWithBookingDetails(stayId)
                .orElseThrow(() -> new BookingNotFoundException(stayId));

        String guestName = stay.getGuest().getFullName();
        Long roomId = stay.getRoom().getId();

        // Lock the owning room before removing a Stay so booking changes stay consistent
        Room room = findRoomWithBookingLock(roomId);

        int roomNumber = room.getNumber();
        String hotelName = room.getHotel().getName();

        if (!room.removeStayById(stayId)) {
            throw new BookingNotFoundException(stayId);
        }

        safeActivityLogger.log(
                ActivityType.DELETE_BOOKING,
                "Booking for " + guestName + " in room " + roomNumber + " at " + hotelName + " cancelled"
        );
    }

    // First query obtains the database write lock on the room row
    // Second query reloads the full aggregate needed by Room.addGuest and logging
    private Room findRoomWithBookingLock(Long roomId) {
        roomRepo.findByIdForUpdate(roomId)
                .orElseThrow(() -> new RoomNotFoundException(roomId));

        return roomRepo.findByIdWithHotelAndGuests(roomId)
                .orElseThrow(() -> new RoomNotFoundException(roomId));
    }

    private Customer getLoggedInCustomer() {
        // Customer email comes from the Spring Security session
        return customerRepo.findByProfileEmail(securityService.getLoggedInUsername())
                .orElseThrow(() -> new IllegalArgumentException("Customer account not found."));
    }
}