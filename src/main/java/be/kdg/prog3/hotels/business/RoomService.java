package be.kdg.prog3.hotels.business;
import be.kdg.prog3.hotels.domain.Guest;
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
    Room createRoom(Room room);

    List<Room> getRoomsByHotel(String hotelId);

    List<Room> getRoomsByNumber(int number);

    Room getRoomById(Long roomId);

    List<Room> getRoomsByGuest(Long guestId);

    void deleteRoom(Long roomId);

    double calculateDiscountedPrice(Room room, Guest guest);

    Room save(Room room);

    void updateRoomDescription(Long roomId, String description);
}
