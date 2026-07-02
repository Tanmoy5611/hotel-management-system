package be.kdg.prog5.hotels.business.ai;

import be.kdg.prog5.hotels.webapi.dto.ai.AiRecommendationResponseDto;

// Business boundary for personalized hotel room recommendations
public interface AiRecommendationService {
    // Uses current customer booking history when available
    AiRecommendationResponseDto getRecommendationsForCurrentCustomer();
}