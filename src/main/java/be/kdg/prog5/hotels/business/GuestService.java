package be.kdg.prog5.hotels.business;

import be.kdg.prog5.hotels.domain.Guest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface GuestService {
    List<Guest> getAllGuests();

    void deleteGuest(Long guestId);

    List<Guest> getVipGuests();

    // Input sanitization happens in the service - controller passes raw values
    List<Guest> searchGuests(String query, Integer minRooms);

    // Service decides Guest vs VIPGuest based on discount - domain decision
    // Controller passes raw form values; service builds the correct domain object
    Guest createGuestWithRoom(String fullName, LocalDate dob, String email, String avatarUrl,
                              BigDecimal discountPercentage, Long roomId,
                              LocalDate checkIn, LocalDate checkOut);

    Guest getGuestWithDetails(Long guestId);
}