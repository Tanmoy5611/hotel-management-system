package be.kdg.prog5.hotels.webapi.dto.ai;

import java.util.List;

// Response shown by the chatbot UI after Python parses and ranks the request
public record AiChatResponseDto(
        // Human-readable assistant answer
        String reply,

        // Structured filters extracted from the customer text
        AiSearchFiltersDto filters,

        // Ranked room cards for the current chat turn
        List<AiRoomSuggestionDto> rooms
) {
}