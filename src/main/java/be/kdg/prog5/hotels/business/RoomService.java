package be.kdg.prog5.hotels.business;

import be.kdg.prog5.hotels.domain.Room;
import be.kdg.prog5.hotels.domain.RoomType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

// Business service for Room aggregate
// Controllers must NEVER talk directly to repositories

public interface RoomService {

    List<Room> getAllRooms();      // List all rooms (rooms overview page)

    // Filter rooms (type, sea view, max price)
    List<Room> findRooms(Optional<RoomType> type,
                         Optional<Boolean> seaView,
                         Optional<BigDecimal> maxPrice);

    // Create new room (belongs to a hotel)
    Room createRoom(Room room, String hotelId);

    // Search rooms by room number
    List<Room> getRoomsByNumber(int roomNumber);

    // Load full Room aggregate (room + hotel + stays + guests)
    // Stays are already sorted by check-in date for the detail page
    Room getRoomById(Long roomId);

    // Delete room (Room aggregate root handles Stay cascade)
    void deleteRoom(Long roomId);

    // Validation + trimming happens here, then JPA dirty checking updates the entity
    void updateRoomDescription(Long roomId, String description);

    /// For Homepage
    List<Room> getBestValueRooms();
    List<Room> getPremiumRooms();
    List<Room> getTopPickedRooms();

    // Search rooms from the home page
    List<Room> searchAvailableRooms(String query,
                                    RoomType roomType,
                                    LocalDate checkIn,
                                    LocalDate checkOut);
}