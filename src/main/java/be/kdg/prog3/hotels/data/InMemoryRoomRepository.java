package be.kdg.prog3.hotels.data;

import be.kdg.prog3.hotels.domain.Room;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

// In-memory implementation of RoomRepository
@Repository
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
}
