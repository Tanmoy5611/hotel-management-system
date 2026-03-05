package be.kdg.prog5.hotels.business.exceptions;

public class RoomAlreadyExistsException extends RuntimeException {

    public RoomAlreadyExistsException(int roomNumber, String hotelId) {
        super("Room number " + roomNumber +
                " already exists in hotel " + hotelId);
    }
}