package be.kdg.prog5.hotels.webapi.dto.ai;

public record AiBookingSessionDto(
        // True only when the current browser session belongs to a customer
        boolean customer
) {
}