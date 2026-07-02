package be.kdg.prog5.hotels.webapi.controller.ai;

import be.kdg.prog5.hotels.business.ai.AiChatService;
import be.kdg.prog5.hotels.webapi.dto.ai.AiChatRequestDto;
import be.kdg.prog5.hotels.webapi.dto.ai.AiChatResponseDto;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai/chat")
public class AiChatApiController {

    // Controller remains thin so AI behavior stays inside the business and Python layers
    private final AiChatService aiChatService;

    public AiChatApiController(AiChatService aiChatService) {
        this.aiChatService = aiChatService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AiChatResponseDto> chat(@RequestBody @Valid AiChatRequestDto request) {
        // Validated browser message is forwarded to the Spring AI service
        return ResponseEntity.ok(aiChatService.findRooms(request.message()));
    }
}