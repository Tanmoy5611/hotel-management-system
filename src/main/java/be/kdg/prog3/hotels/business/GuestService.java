package be.kdg.prog3.hotels.business;
import be.kdg.prog3.hotels.domain.Guest;
import java.util.List;

public interface GuestService {
    List<Guest> getAllGuests();

    List<Guest> getGuestsByRoom(int roomNumber);

    Guest createGuest(Guest guest);

    Guest getGuestById(long id);

    void deleteGuest(long id);

    List<Guest> getVipGuests();

    List<Guest> searchGuestsByName(String name);

    List<Guest> getGuestsWithManyRooms(int minRooms);

    Guest createGuestWithRoom(Guest guest, Integer roomNumber);
}