package be.kdg.prog5.hotels.business;

import be.kdg.prog5.hotels.domain.Guest;

import java.time.LocalDate;
import java.util.List;

public interface GuestService {
    List<Guest> getAllGuests();

    List<Guest> getGuestsByRoom(Long roomId);

    void deleteGuest(Long guestId);

    List<Guest> getVipGuests();

    List<Guest> searchGuestsByName(String name);

    List<Guest> getGuestsWithManyRooms(int minRooms);

    Guest createGuestWithRoom(Guest guest, Long roomId, LocalDate checkIn, LocalDate checkOut);

    Guest getGuestWithDetails(Long guestId);
}