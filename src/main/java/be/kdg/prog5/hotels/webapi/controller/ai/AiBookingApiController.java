package be.kdg.prog5.hotels.webapi.controller.ai;

import be.kdg.prog5.hotels.business.ai.AiBookingService;
import be.kdg.prog5.hotels.business.security.SecurityService;
import be.kdg.prog5.hotels.webapi.dto.ai.AiBookingActionResponseDto;
import be.kdg.prog5.hotels.webapi.dto.ai.AiBookingCancelRequestDto;
import be.kdg.prog5.hotels.webapi.dto.ai.AiBookingConfirmRequestDto;
import be.kdg.prog5.hotels.webapi.dto.ai.AiBookingQuoteRequestDto;
import be.kdg.prog5.hotels.webapi.dto.ai.AiBookingQuoteResponseDto;
import be.kdg.prog5.hotels.webapi.dto.ai.AiBookingSessionDto;
import be.kdg.prog5.hotels.webapi.dto.ai.AiBookingSummaryDto;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ai/bookings")
public class AiBookingApiController {
    // Controller stays thin so booking rules remain in the business service
    private final AiBookingService aiBookingService;

    // Session check is public so the browser can decide whether to show login guidance
    private final SecurityService securityService;

    public AiBookingApiController(AiBookingService aiBookingService, SecurityService securityService) {
        this.aiBookingService = aiBookingService;
        this.securityService = securityService;
    }

    @GetMapping(value = "/session", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AiBookingSessionDto> session() {
        // Public endpoint used by the chatbot before it starts protected booking flows
        return ResponseEntity.ok(new AiBookingSessionDto(securityService.isCustomer()));
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping(value = "/quote", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AiBookingQuoteResponseDto> quote(@RequestBody @Valid AiBookingQuoteRequestDto request) {
        // Quote checks availability and price without creating a booking
        return ResponseEntity.ok(aiBookingService.quote(request));
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping(value = "/confirm", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AiBookingActionResponseDto> confirm(@RequestBody @Valid AiBookingConfirmRequestDto request) {
        // Confirmation creates the stay only after the customer accepts the quote
        return ResponseEntity.ok(aiBookingService.confirm(request));
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AiBookingSummaryDto>> currentBookings() {
        // The chat cancel flow uses this compact list instead of loading a full page
        return ResponseEntity.ok(aiBookingService.currentCustomerBookings());
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping(value = "/cancel", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AiBookingActionResponseDto> cancel(@RequestBody @Valid AiBookingCancelRequestDto request) {
        return ResponseEntity.ok(aiBookingService.cancel(request));
    }
}