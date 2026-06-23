package be.kdg.prog5.hotels.business.exceptions;

// Signals a duplicate email during user creation
public class ApplicationUserAlreadyExistsException extends RuntimeException {
    public ApplicationUserAlreadyExistsException(String email) {
        super("A user with email " + email + " already exists");
    }
}