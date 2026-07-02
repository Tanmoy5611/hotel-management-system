package be.kdg.prog5.hotels.webapi.dto.ai;

import java.util.List;

// Personal recommendation response returned to the customer dashboard
public record AiRecommendationResponseDto(
        // Empty list means no valid booking-history signal exists yet
        List<AiRoomSuggestionDto> recommendations
) {
}