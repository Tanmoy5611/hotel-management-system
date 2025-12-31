package be.kdg.prog3.hotels.data.inmemory;

import be.kdg.prog3.hotels.data.DataFactory;
import be.kdg.prog3.hotels.data.HotelRepository;
import be.kdg.prog3.hotels.domain.Hotel;
import be.kdg.prog3.hotels.domain.Room;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;


// In-memory implementation of HotelRepository
// (instead of using a database, it works using Java Lists inside DataFactory)
@Repository
@Profile("inmemory")
public class InMemoryHotelRepository implements HotelRepository {
    private static final Logger log = LoggerFactory.getLogger(InMemoryHotelRepository.class);

    // Counter for generating unique hotel IDs (Fake Auto-Increment)
   // private final AtomicInteger seq = new AtomicInteger(1000);

    /* In-memory data is NOT stored as a list of hotels.
    Instead: DataFactory.rooms = List<Room>
    Each Room has a reference → Room.getHotel()  */
    @Override
    public List<Hotel> findAll() {
        log.debug("Reading hotels from DataFactory (in-memory)");
        return DataFactory.hotels;

//        return DataFactory.rooms.stream()
//                .map(Room::getHotel)       // take the hotel from each room
//                .filter(Objects::nonNull)  // some rooms may not have hotel(Skip)
//                .distinct()                // avoiding duplicate hotels
//                .toList();
    }

    /* Since this is IN-MEMORY and do NOT actually store hotels in a DB,
        the save simply returns the hotel. The real final storage comes from
        Room objects referencing hotels Ex: hotel.addRoom(room) */
    @Override
    public Hotel save(Hotel hotel) {
        log.debug("Saving hotel: {}", hotel);

        // Avoid duplicates
        DataFactory.hotels.removeIf(h -> h.getId().equals(hotel.getId()));
        DataFactory.hotels.add(hotel);

        return hotel;
    }

    /* DataFactory.hotels *does* contain the initial hotel list
       and it simply search that list */
    @Override
    public Hotel findHotelById(String id) {
        return DataFactory.hotels.stream()
                .filter(h -> h.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    // delete the hotel from DataFactory.hotels AND remove all associated rooms belonging to that hotel.
    // This simulates cascade delete in JPA.
    @Override
    public void delete(String id) {
        // remove hotel
        DataFactory.hotels.removeIf(h -> h.getId().equals(id));

        // also remove rooms belonging to this hotel
        DataFactory.rooms.removeIf(r ->
                r.getHotel() != null &&
                r.getHotel().getId().equals(id));
    }
}
