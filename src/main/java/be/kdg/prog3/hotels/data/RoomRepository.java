package be.kdg.prog3.hotels.data;

import be.kdg.prog3.hotels.domain.Room;
import java.util.List;

public interface RoomRepository {
    List<Room> findAll();

}
