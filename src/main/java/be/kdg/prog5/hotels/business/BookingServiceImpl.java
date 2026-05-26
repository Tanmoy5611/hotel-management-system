package be.kdg.prog5.hotels.business;

import be.kdg.prog5.hotels.business.exceptions.BookingNotFoundException;
import be.kdg.prog5.hotels.business.exceptions.GuestNotFoundException;
import be.kdg.prog5.hotels.business.exceptions.RoomNotFoundException;
import be.kdg.prog5.hotels.data.SpringDataGuestRepository;
import be.kdg.prog5.hotels.data.SpringDataRoomRepository;
import be.kdg.prog5.hotels.data.SpringDataStayRepository;
import be.kdg.prog5.hotels.domain.ActivityType;
import be.kdg.prog5.hotels.domain.Guest;
import be.kdg.prog5.hotels.domain.Room;
import be.kdg.prog5.hotels.domain.Stay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class BookingServiceImpl implements BookingService {

    private static final Logger log = LoggerFactory.getLogger(BookingServiceImpl.class);

    private final SpringDataRoomRepository roomRepo;
    private final SpringDataGuestRepository guestRepo;
    private final SpringDataStayRepository stayRepo;
    private final SafeActivityLogger safeActivityLogger;

    // Injects repositories needed to coordinate booking use cases
    public BookingServiceImpl(SpringDataRoomRepository roomRepo,
                              SpringDataGuestRepository guestRepo,
                              SpringDataStayRepository stayRepo,
                              SafeActivityLogger safeActivityLogger) {
        this.roomRepo = roomRepo;
        this.guestRepo = guestRepo;
        this.stayRepo = stayRepo;
        this.safeActivityLogger = safeActivityLogger;
    }

    // Returns current and future bookings with guest, room, and hotel data already loaded
    @Override
    @Transactional(readOnly = true)
    public List<Stay> getCurrentBookings() {
        log.debug("Getting current bookings");
        return stayRepo.findCurrentBookingsWithDetails(LocalDate.now());
    }


    // Books a room through the Room aggregate and evicts guest search cache because guest stay counts can change
    @Override
    @CacheEvict(value = "guestSearch", allEntries = true)
    public void bookRoom(Long roomId, Long guestId,
                         LocalDate checkIn,
                         LocalDate checkOut) {
        log.debug("Booking room {} for guest {}", roomId, guestId);

        Room room = roomRepo.findByIdWithHotelAndGuests(roomId)
                .orElseThrow(() -> new RoomNotFoundException(roomId));

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

    // Cancels a booking by removing the Stay from the managed Room aggregate and logging the admin action
    @Override
    @CacheEvict(value = "guestSearch", allEntries = true)
    public void cancelBooking(Long stayId) {
        log.debug("Cancelling booking {}", stayId);

        Stay stay = stayRepo.findByIdWithBookingDetails(stayId)
                .orElseThrow(() -> new BookingNotFoundException(stayId));

        String guestName = stay.getGuest().getFullName();
        Long roomId = stay.getRoom().getId();

        Room room = roomRepo.findByIdWithHotelAndGuests(roomId)
                .orElseThrow(() -> new RoomNotFoundException(roomId));

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
}