package be.kdg.prog3.hotels.business;

import be.kdg.prog3.hotels.domain.Room;
import be.kdg.prog3.hotels.domain.RoomType;

import java.util.List;
import java.util.Optional;

// Interface for Room Service
public interface RoomService {
    List<Room> getAllRooms();  // Return all rooms

    // Return rooms filtered by type, sea view and max price
    List<Room> findRooms(Optional<RoomType> type, Optional<Boolean> seaView, Optional<Double> maxPrice);

    // Creates a new room and saves it to the database
    Room createdRoom(Room room);
}
