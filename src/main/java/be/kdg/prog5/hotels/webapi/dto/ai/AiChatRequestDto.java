package be.kdg.prog5.hotels.webapi.dto.ai;

import jakarta.validation.constraints.NotBlank;

// Browser request DTO for the floating AI chat widget
public record AiChatRequestDto(
        // Prevent empty messages from reaching the Python parser
        @NotBlank(message = "Message is required")
        String message
) {
}