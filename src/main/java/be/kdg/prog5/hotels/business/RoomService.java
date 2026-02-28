package be.kdg.prog5.hotels.business;

import be.kdg.prog5.hotels.domain.Guest;
import be.kdg.prog5.hotels.domain.Room;
import be.kdg.prog5.hotels.domain.RoomType;
import be.kdg.prog5.hotels.domain.Stay;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

// Business service for Room aggregate.
// Controllers must NEVER talk directly to repositories.

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
    Room getRoomById(Long roomId);

    // Delete room (Room aggregate root handles Stay cascade)
    void deleteRoom(Long roomId);

    // Update description using JPA dirty checking
    void updateRoomDescription(Long roomId, String description);

    void bookRoom(Long roomId, Long guestId, LocalDate checkIn, LocalDate checkOut);   // aggregate operation

    /// For Homepage
    List<Room> getBestValueRooms();
    List<Room> getPremiumRooms();
    List<Room> getTopPickedRooms();
}
