package be.kdg.prog3.hotels.data.inmemory;

import be.kdg.prog3.hotels.data.DataFactory;
import be.kdg.prog3.hotels.data.GuestRepository;
import be.kdg.prog3.hotels.domain.Guest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

// In-memory implementation of GuestRepository

@Repository
@Profile("inmemory")
public class InMemoryGuestRepository implements GuestRepository {

    private static final Logger log = LoggerFactory.getLogger(InMemoryGuestRepository.class);

    // Use the existing DataFactory list (thread-safe wrapper)
//    private final List<Guest> guests = new CopyOnWriteArrayList<>(DataFactory.guests);
//
//    private long nextId = 1;


    private final List<Guest> guests;

    private long nextId;

    public InMemoryGuestRepository() {
        // Load seeded data ONCE
        this.guests = new CopyOnWriteArrayList<>(DataFactory.guests);

        // Determine next ID safely
        long maxId = guests.stream()
                .mapToLong(Guest::getId)
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

        if (guest.getId() == 0) {   // new guest added
            guest.setId(nextId++);
        }

        guests.removeIf(g -> g.getId() == guest.getId()); // update old one
        guests.add(guest);

        return guest;
    }


    @Override
    public Guest findById(long id) {
        return guests.stream()
                .filter(g -> g.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Guest> findByRoom(int roomNumber) {
        return guests.stream()
                .filter(g -> g.getRooms().stream()
                        .anyMatch(r -> r.getNumber() == roomNumber))
                .toList();
    }

    @Override
    public void delete(long id) {
        guests.removeIf(g -> g.getId() == id);

        // also remove from rooms list (many-to-many)
        DataFactory.rooms.forEach(room ->
                room.getGuests().removeIf(g -> g.getId() == id)
        );
    }

}