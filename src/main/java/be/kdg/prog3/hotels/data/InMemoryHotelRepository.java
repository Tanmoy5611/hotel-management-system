package be.kdg.prog3.hotels.data;

import be.kdg.prog3.hotels.domain.Hotel;
import be.kdg.prog3.hotels.domain.Room;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;


// In-memory implementation of HotelRepository
@Repository
public class InMemoryHotelRepository implements HotelRepository {
    private static final Logger log = LoggerFactory.getLogger(InMemoryHotelRepository.class);

    // Counter for generating unique hotel IDs
    private final AtomicInteger seq = new AtomicInteger(1000);

    // retrieves distinct  hotels from the list of rooms
    @Override
    public List<Hotel> findAll() {
        log.debug("Reading distinct hotels from rooms list");
        return DataFactory.rooms.stream()
                .map(Room::getHotel)       // tale hotel from each room
                .filter(Objects::nonNull)  // skip null hotels
                .distinct()                // avoiding duplicates
                .toList();
    }

    // saves a hotel
    @Override
    public Hotel save(Hotel hotel) {
        log.debug("Saving hotel: {}", hotel);
        return hotel;  // In-memory “save”; list of hotels derived from rooms; adding rooms later will show it.
    }

}
