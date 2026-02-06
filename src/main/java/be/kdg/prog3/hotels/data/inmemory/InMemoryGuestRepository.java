package be.kdg.prog3.hotels.data.inmemory;

import be.kdg.prog3.hotels.data.DataFactory;
import be.kdg.prog3.hotels.data.GuestRepository;
import be.kdg.prog3.hotels.domain.Guest;
import be.kdg.prog3.hotels.domain.Room;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

// In-memory implementation of GuestRepository

@Repository
@Profile("inmemory")
public class InMemoryGuestRepository implements GuestRepository {

    private static final Logger log = LoggerFactory.getLogger(InMemoryGuestRepository.class);

    private final List<Guest> guests;

    private Long nextId;    // wrapper type

    public InMemoryGuestRepository() {
        // Load data
        this.guests = DataFactory.guests;

        // Determine next ID safely
        // Find the highest existing id (ignore null ids)
        long maxId = guests.stream()
                .map(Guest::getId)
                .filter(Objects::nonNull)
                .mapToLong(Long::longValue)
                .max()
                .orElse(0);

        this.nextId = maxId + 1;


        log.info("InMemoryGuestRepository initialized with {} guests, nextId={}",
                guests.size(), nextId);
    }


    @Override
    public List<Guest> findAll() {
        log.debug("Reading all guests from DataFactory (in-memory)");
        return guests;
    }

    @Override
    public Guest save(Guest guest) {
        log.debug("Saving guest (in-memory): {}", guest.getFullName());

        if (guest.getId() == null) {
            guest.setId(nextId++);
        }

        guests.removeIf(g -> g.getId().equals(guest.getId())); // update or insert
        guests.add(guest);

        return guest;
    }


    @Override
    public Guest findById(Long id) {
        return guests.stream()
                .filter(g -> g.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Guest> findByRoom(Long roomId) {
        return guests.stream()
                .filter(g -> g.getRooms().stream()
                        .anyMatch(r -> r.getId().equals(roomId)))
                .toList();
    }

    @Override
    public void delete(Long id) {
        guests.removeIf(g -> g.getId().equals(id));  // equals for Long

        // also remove from rooms list (many-to-many)
        DataFactory.rooms.forEach(room ->
                room.getGuests().removeIf(g -> g.getId().equals(id))
        );
    }

    @Override
    public void addGuestToRoom(Long guestId, Long roomId) {
        Guest guest = findById(guestId);
        Room room = DataFactory.rooms.stream()
                .filter(r -> r.getId().equals(roomId))
                .findFirst()
                .orElse(null);

        if (guest == null || room == null) return;

        if (!guest.getRooms().contains(room)) {
            guest.getRooms().add(room);
            room.getGuests().add(guest);
        }
    }
}