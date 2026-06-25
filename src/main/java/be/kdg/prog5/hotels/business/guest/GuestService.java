package be.kdg.prog5.hotels.business.guest;

import be.kdg.prog5.hotels.domain.Guest;
import org.springframework.security.access.prepost.PreAuthorize;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface GuestService {
    List<Guest> getAllGuests();

    // Owner or admin only - checked before the service method executes
    @PreAuthorize("@guestAuthorizationService.canDeleteGuest(#guestId, authentication)")
    void deleteGuest(Long guestId);

    List<Guest> getVipGuests();

    // Input sanitization happens in the service - controller passes raw values
    List<Guest> searchGuests(String query, Integer minRooms);

    // Service decides Guest vs VIPGuest based on discount - domain decision
    // Controller passes raw form values; service builds the correct domain object
    Guest createGuestWithRoom(String fullName, LocalDate dob, String email, String avatarUrl,
                              BigDecimal discountPercentage, Long roomId,
                              LocalDate checkIn, LocalDate checkOut);

    // Week 10 Client API: creates a guest without a room from the separate frontend client
    Guest createGuestFromClient(String fullName, LocalDate dob, String email, String avatarUrl,
                                BigDecimal discountPercentage);

    Guest getGuestWithDetails(Long guestId);

    // Returns guest data already prepared for the detail page
    GuestDetails getGuestDetails(Long guestId);
}