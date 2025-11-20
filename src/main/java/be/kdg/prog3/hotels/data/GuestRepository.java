package be.kdg.prog3.hotels.data;

import be.kdg.prog3.hotels.domain.Guest;
import java.util.List;

// Common interface for both InMemory and JDBC repositories
public interface GuestRepository {
    List<Guest> findAll();
    Guest save(Guest guest);
}