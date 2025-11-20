package be.kdg.prog3.hotels.data;

import be.kdg.prog3.hotels.domain.Guest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * In-memory implementation of GuestRepository.
 * Works when profile = "inmemory".
 * Data comes directly from DataFactory (just like Hotels & Rooms).
 * Author: Tanmoy ✨
 */
@Repository
@Profile("inmemory")
public class InMemoryGuestRepository implements GuestRepository {

    private static final Logger log = LoggerFactory.getLogger(InMemoryGuestRepository.class);

    // Use the existing DataFactory list (thread-safe wrapper)
    private final List<Guest> guests = new CopyOnWriteArrayList<>(DataFactory.guests);

    @Override
    public List<Guest> findAll() {
        log.debug("Reading all guests from DataFactory (in-memory)");
        return guests;
    }

    @Override
    public Guest save(Guest guest) {
        log.debug("Saving guest (in-memory): {}", guest.getFullName());
        guests.add(guest);
        return guest; // FOLLOWING HotelRepository style
    }
}