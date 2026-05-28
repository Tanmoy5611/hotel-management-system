package be.kdg.prog5.hotels.business.exceptions;

public class ApplicationUserHasGuestsException extends RuntimeException {
    public ApplicationUserHasGuestsException(String email) {
        super("User " + email + " cannot be deleted because they still own guests");
    }
}