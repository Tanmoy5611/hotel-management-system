package be.kdg.prog3.hotels.business;

import be.kdg.prog3.hotels.data.RoomRepository;
import be.kdg.prog3.hotels.domain.Room;
import be.kdg.prog3.hotels.domain.RoomType;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class RoomServiceImpl implements RoomService {
    private final RoomRepository repo;

    public RoomServiceImpl(RoomRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<Room> getAllRooms() {
        return repo.findAll();
    }

    @Override
    public List<Room> findRooms(Optional<RoomType> type, Optional<Boolean> seaView, Optional<Double> maxPrice) {
        return repo.findAll().stream()
                .filter(r -> type.map(t -> r.getType() == t).orElse(true))
                .filter(r -> seaView.map(b -> r.isSeaView() == b).orElse(true))
                .filter(r -> maxPrice.map(p -> r.getPricePerNight() <= p).orElse(true))
                .toList();


    }
}
