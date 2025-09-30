package be.kdg.prog3.hotels.data;

import be.kdg.prog3.hotels.domain.Room;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class InMemoryRoomRepository implements RoomRepository {
    @Override
    public List<Room> findAll() {
        return List.copyOf(DataFactory.rooms);
    }
}
