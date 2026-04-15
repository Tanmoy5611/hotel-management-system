package be.kdg.prog5.hotels.business.exceptions;

public class HotelNotFoundException extends RuntimeException {
    public HotelNotFoundException(String hotelId) {
        super("Hotel not found with id: " + hotelId);
    }
}