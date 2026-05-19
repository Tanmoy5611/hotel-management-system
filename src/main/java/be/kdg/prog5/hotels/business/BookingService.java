package be.kdg.prog5.hotels.business;

import be.kdg.prog5.hotels.domain.Stay;

import java.time.LocalDate;
import java.util.List;

public interface BookingService {
    // Loads active bookings for the admin booking overview
    List<Stay> getCurrentBookings();

    // Creates a room booking and keeps booking logic outside RoomService
    void bookRoom(Long roomId, Long guestId, LocalDate checkIn, LocalDate checkOut);

    // Cancels one booking by removing its Stay from the owning Room aggregate
    void cancelBooking(Long stayId);
}