package be.kdg.prog5.hotels.business.exceptions;

public class BookingNotFoundException extends RuntimeException {
    // Creates a clear 404 message when an admin tries to cancel a missing booking
    public BookingNotFoundException(Long bookingId) {
        super("Booking not found with id " + bookingId);
    }
}