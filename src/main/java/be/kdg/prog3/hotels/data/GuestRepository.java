package be.kdg.prog3.hotels.data;
import be.kdg.prog3.hotels.domain.Guest;
import java.util.List;

// Common interface for both InMemory and JDBC repositories
public interface GuestRepository {
    List<Guest> findAll();

    Guest save(Guest guest);

    Guest findById(Long id);

    // for many-to-many query
    List<Guest> findByRoom(Long roomId);

    void delete(Long id);

    void addGuestToRoom(Long guestId, Long roomId);
}