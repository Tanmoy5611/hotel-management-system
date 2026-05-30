package be.kdg.prog5.hotels.business.exceptions;

public class GuestAlreadyExistsException extends RuntimeException {
    public GuestAlreadyExistsException(String email) {
        super("A guest with email " + email + " already exists");
    }
}