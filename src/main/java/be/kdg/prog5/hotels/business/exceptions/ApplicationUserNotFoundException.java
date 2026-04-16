package be.kdg.prog5.hotels.business.exceptions;

public class ApplicationUserNotFoundException extends RuntimeException {
    public ApplicationUserNotFoundException(Long id) {
        super("Hotel not found with id: " + id);
    }
}