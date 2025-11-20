package be.kdg.prog3.hotels.business;

import be.kdg.prog3.hotels.domain.Guest;
import java.util.List;

public interface GuestService {
    List<Guest> getAllGuests();
    Guest createGuest(Guest guest);
}