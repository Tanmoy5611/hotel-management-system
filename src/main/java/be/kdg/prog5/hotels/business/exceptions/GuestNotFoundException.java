package be.kdg.prog5.hotels.business.exceptions;

public class GuestNotFoundException extends RuntimeException {
    public GuestNotFoundException(Long guestId) {
        super("Guest not found with id " + guestId);
    }
}