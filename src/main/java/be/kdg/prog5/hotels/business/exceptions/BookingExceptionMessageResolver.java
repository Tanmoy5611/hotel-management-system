package be.kdg.prog5.hotels.business.exceptions;

public final class BookingExceptionMessageResolver {
    private BookingExceptionMessageResolver() {
    }

    public static String toMessage(String code) {
        return switch (code) {
            case "booking.dates.required" -> "Please provide both check-in and check-out dates.";
            case "booking.checkout.after.checkin" -> "Check-out must be after check-in.";
            case "booking.past.not.allowed" -> "Check-in cannot be in the past.";
            case "booking.overlap.not.allowed" -> "That room is not available for those dates.";
            default -> "The booking request could not be completed.";
        };
    }
}
