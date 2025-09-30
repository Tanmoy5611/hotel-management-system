package be.kdg.prog3.hotels.business;

import be.kdg.prog3.hotels.domain.Room;
import be.kdg.prog3.hotels.domain.RoomType;

import java.util.List;
import java.util.Optional;

public interface RoomService {
    List<Room> getAllRooms();
    List<Room> findRooms(Optional<RoomType> type, Optional<Boolean> seaView, Optional<Double> maxPrice);
}
