package be.kdg.prog5.hotels.webapi.dto.ai;

import java.util.List;

// Internal DTO sent from Spring to the Python chatbot endpoint
public record AiChatPythonRequestDto(
        // Raw user text after basic Java-side trimming
        String message,

        // Current room catalog used by Python for text-based search ranking
        List<AiRoomFeatureDto> availableRooms
) {
}