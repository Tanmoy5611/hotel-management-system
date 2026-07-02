package be.kdg.prog5.hotels.business.ai;

import be.kdg.prog5.hotels.webapi.dto.ai.AiChatResponseDto;

// Business boundary for natural-language room search
public interface AiChatService {
    // Finds matching rooms from a customer message
    AiChatResponseDto findRooms(String message);
}