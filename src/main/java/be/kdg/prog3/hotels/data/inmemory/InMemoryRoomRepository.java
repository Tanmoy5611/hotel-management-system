package be.kdg.prog3.hotels.data.inmemory;
import be.kdg.prog3.hotels.data.DataFactory;
import be.kdg.prog3.hotels.data.RoomRepository;
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
    public Room save(Room room) {
        log.debug("Saving room: {}", room);

        // manual ID generation
        if (room.getId() == null) {
            long nextId = DataFactory.rooms.stream()
                    .mapToLong(r -> r.getId() == null ? 0 : r.getId())
                    .max()
                    .orElse(0) + 1;
            room.setId(nextId);
        }

        // Enforce uniqueness: same hotel + same room number
        DataFactory.rooms.removeIf(r ->
                r.getHotel().getId().equals(room.getHotel().getId())
                        && r.getNumber() == room.getNumber()
        );

        DataFactory.rooms.add(room);
        return room;
    }

    // find by Room ID (primary key)
    @Override
    public Room findById(Long id) {
        return DataFactory.rooms.stream()
                .filter(r -> r.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Room> findByHotel(String hotelId) {
        return DataFactory.rooms.stream()
                .filter(r -> r.getHotel() != null)
                .filter(r -> r.getHotel().getId().equals(hotelId))
                .toList();
    }

    @Override
    public List<Room> findByGuest(Long guestId) {
        return DataFactory.rooms.stream()
                .filter(r -> r.getGuests().stream()
                        .anyMatch(g -> g.getId().equals(guestId)))
                .toList();
    }

    @Override
    public void delete(Long id) {
        DataFactory.rooms.removeIf(r -> r.getId().equals(id));
    }
}
