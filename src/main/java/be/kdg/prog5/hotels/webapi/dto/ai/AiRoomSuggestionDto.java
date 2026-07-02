package be.kdg.prog5.hotels.webapi.dto.ai;

import java.math.BigDecimal;

// Ranked room returned by the AI service
public record AiRoomSuggestionDto(
        // Identifiers needed by the frontend to link to the selected room
        Long roomId,
        String hotelId,

        // Display fields kept here so the UI does not fetch room details again
        String hotelName,
        int roomNumber,
        String city,
        String roomType,
        BigDecimal pricePerNight,

        // Score is normalized between 0 and 1 by Python
        double score,

        // Explainable reason shown below the recommendation card
        String reason
) {
}