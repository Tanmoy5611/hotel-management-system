package be.kdg.prog3.hotels.data;

import be.kdg.prog3.hotels.domain.Room;
import java.util.List;

// Repository interface for Room entity
public interface RoomRepository {
    List<Room> findAll();   // Return all stored rooms

    Room save(Room room);  // Saves new room to the database

    Room findById(int number);

    List<Room> findByHotel(String hotelId);

    List<Room> findByGuest(long guestId);

    void delete(int number);
}
