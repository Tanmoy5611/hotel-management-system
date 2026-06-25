package be.kdg.prog5.hotels.business.booking;

import be.kdg.prog5.hotels.domain.Stay;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

public interface BookingService {
    // Loads active bookings for the admin booking overview
    List<Stay> getCurrentBookings();

    // Loads active bookings with pagination and search for the admin page
    Page<Stay> getCurrentBookings(int page, String search);

    // Cleans search text before it is passed to the repository
    String normalizeSearch(String search);

    // Builds compact page numbers so controllers do not calculate pagination
    List<Integer> getVisiblePageNumbers(Page<Stay> bookingPage);

    // Prepares booking form data based on the logged in user type
    BookingFormDetails getBookingFormDetails(Long roomId);

    // Creates a room booking and keeps booking logic outside RoomService
    void bookRoom(Long roomId, Long guestId, LocalDate checkIn, LocalDate checkOut);

    // Customer bookings ignore guestId and always use the customer profile
    void bookRoomForCurrentUser(Long roomId, Long guestId, LocalDate checkIn, LocalDate checkOut);

    // Cancels one booking by removing its Stay from the owning Room aggregate
    void cancelBooking(Long stayId);
}