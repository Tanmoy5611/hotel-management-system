package be.kdg.prog3.hotels.business.exceptions;

// Custom business exception - thrown when a room with a given number does not exist
public class RoomNotFoundException extends RuntimeException {

    public RoomNotFoundException(int roomNumber) {
        super("Room with number " + roomNumber + " was not found");
    }
}