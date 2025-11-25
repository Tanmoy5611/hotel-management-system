package be.kdg.prog3.hotels.data;

import be.kdg.prog3.hotels.domain.Room;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

// In-memory implementation of RoomRepository
@Repository
@Profile("inmemory")
public class InMemoryRoomRepository implements RoomRepository {
    private static final Logger log = LoggerFactory.getLogger(InMemoryRoomRepository.class);


    // Returns all rooms from the data factory
    @Override
    public List<Room> findAll() {
        log.debug("Reading rooms: {} found", DataFactory.rooms.size());
        return List.copyOf(DataFactory.rooms);

    }

    // Adds new room to the in-memory list
    @Override
    public Room save (Room room) {
        log.debug("Saving room: {}", room);
        DataFactory.rooms.add(room);
        return room;
    }

    @Override
    public Room findById(int number) {
        return DataFactory.rooms.stream()
                .filter(r -> r.getNumber() == number)
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Room> findByHotel(String hotelId) {
        return DataFactory.rooms.stream()
                .filter(r -> r.getHotel().getId().equals(hotelId))
                .toList();
    }

    @Override
    public List<Room> findByGuest(long guestId) {
        return DataFactory.guests.stream()
                .filter(g -> g.getId() == guestId)
                .findFirst()
                .map(g -> List.copyOf(g.getRooms()))
                .orElse(List.of());
    }

    @Override
    public void delete(int number) {
        DataFactory.rooms.removeIf(r -> r.getNumber() == number);
    }
}
