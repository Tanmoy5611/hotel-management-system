package be.kdg.prog5.hotels.webapi.dto.ai;

import java.util.List;

// Request contract for the Python personal recommendation endpoint
public record AiRecommendationRequestDto(
        // Optional identifier used for traceability, not for database access in Python
        Long customerId,

        // Rooms previously booked by the current customer
        List<AiRoomFeatureDto> pastBookings,

        // Rooms available for the model to rank
        List<AiRoomFeatureDto> candidateRooms
) {
}