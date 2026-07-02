package be.kdg.prog5.hotels.webapi.dto.ai;

// Simple result message for chatbot booking mutations
public record AiBookingActionResponseDto(
        boolean success,
        String message
) {
}