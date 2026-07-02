package be.kdg.prog5.hotels.webapi.controller.ai;

import be.kdg.prog5.hotels.business.ai.AiRecommendationService;
import be.kdg.prog5.hotels.webapi.dto.ai.AiRecommendationResponseDto;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai/recommendations")
public class AiRecommendationApiController {

    // Recommendation endpoint is read-only and based on the logged-in customer
    private final AiRecommendationService aiRecommendationService;

    public AiRecommendationApiController(AiRecommendationService aiRecommendationService) {
        this.aiRecommendationService = aiRecommendationService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AiRecommendationResponseDto> getRecommendations() {
        // Business service decides whether enough booking history exists for personalization
        return ResponseEntity.ok(aiRecommendationService.getRecommendationsForCurrentCustomer());
    }
}