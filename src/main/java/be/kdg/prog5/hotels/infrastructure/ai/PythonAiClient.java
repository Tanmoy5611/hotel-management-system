package be.kdg.prog5.hotels.infrastructure.ai;

import be.kdg.prog5.hotels.business.ai.AiServiceUnavailableException;
import be.kdg.prog5.hotels.webapi.dto.ai.AiChatPythonRequestDto;
import be.kdg.prog5.hotels.webapi.dto.ai.AiChatResponseDto;
import be.kdg.prog5.hotels.webapi.dto.ai.AiRecommendationRequestDto;
import be.kdg.prog5.hotels.webapi.dto.ai.AiRecommendationResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class PythonAiClient {

    // RestClient is the only place where Spring talks to the Python FastAPI service
    private final RestClient restClient;

    public PythonAiClient(RestClient.Builder restClientBuilder,
                          @Value("${hotel.ai.service.base-url:http://localhost:8001}") String baseUrl) {
        // Base URL is configurable so local development and deployment can use different AI hosts
        this.restClient = restClientBuilder
                .baseUrl(baseUrl)
                .build();
    }

    public AiRecommendationResponseDto getRecommendations(AiRecommendationRequestDto request) {
        try {
            // Spring sends plain feature DTOs, Python owns the recommendation algorithm
            return restClient.post()
                    .uri("/ai/recommendations")
                    .body(request)
                    .retrieve()
                    .body(AiRecommendationResponseDto.class);
        } catch (RestClientException ex) {
            // Convert HTTP or connection failures into a business exception the web layer understands
            throw new AiServiceUnavailableException("The AI recommendation service is currently unavailable.", ex);
        }
    }

    public AiChatResponseDto chat(AiChatPythonRequestDto request) {
        try {
            // Chat uses a separate endpoint because it parses text before ranking rooms
            return restClient.post()
                    .uri("/ai/chat")
                    .body(request)
                    .retrieve()
                    .body(AiChatResponseDto.class);
        } catch (RestClientException ex) {
            // The UI can show a friendly message without knowing RestClient details
            throw new AiServiceUnavailableException("The AI room finder service is currently unavailable.", ex);
        }
    }
}