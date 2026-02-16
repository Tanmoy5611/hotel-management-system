package be.kdg.prog5.hotels.business.exceptions;

// Custom business exception - thrown when a room with a given number does not exist
public class RoomNotFoundException extends RuntimeException {

    // when searching by room number (business)
    public RoomNotFoundException(int roomNumber) {
        super("Room with number " + roomNumber + " was not found");
    }

    // when searching by room ID (technical)
    public RoomNotFoundException(Long roomId) {
        super("Room with id " + roomId + " was not found");
    }
}