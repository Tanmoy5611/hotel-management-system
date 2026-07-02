package be.kdg.prog5.hotels.webapi.dto.ai;

import jakarta.validation.constraints.NotNull;

// Cancel request for one customer-owned booking
public record AiBookingCancelRequestDto(
        @NotNull(message = "Booking is required")
        Long stayId
) {
}