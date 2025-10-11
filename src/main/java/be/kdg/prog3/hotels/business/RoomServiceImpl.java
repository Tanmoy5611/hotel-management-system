package be.kdg.prog3.hotels.business;

import be.kdg.prog3.hotels.data.RoomRepository;
import be.kdg.prog3.hotels.domain.Room;
import be.kdg.prog3.hotels.domain.RoomType;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class RoomServiceImpl implements RoomService {
    private static final Logger log = LoggerFactory.getLogger(RoomServiceImpl.class);
    private final RoomRepository repo;

    // Injects repository for data access
    public RoomServiceImpl(RoomRepository repo) {
        this.repo = repo;
    }

    // Returns all rooms
    @Override
    public List<Room> getAllRooms() {
        log.debug("Get all rooms()");
        return repo.findAll();
    }

    /// Returns rooms filtering with parameters like type, sea view, and max price
    @Override
    public List<Room> findRooms(Optional<RoomType> type, Optional<Boolean> seaView, Optional<Double> maxPrice) {
        log.debug("Find rooms(type={}, seaView={}, maxPrice={})", type, seaView, maxPrice);

        return repo.findAll().stream()
                .filter(r -> type.map(t -> r.getType() == t).orElse(true))
                .filter(r -> seaView.map(b -> r.isSeaView() == b).orElse(true))
                .filter(r -> maxPrice.map(p -> r.getPricePerNight() <= p).orElse(true))
                .toList();


    }

    // Adds room to repository and returns it
    @Override
    public Room createdRoom(Room room) {
        log.debug("Creating room: {}", room);
        return repo.save(room);
    }
}
