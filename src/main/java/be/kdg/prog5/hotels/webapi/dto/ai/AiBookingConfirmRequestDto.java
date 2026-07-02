package be.kdg.prog5.hotels.webapi.dto.ai;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

// Final booking request sent only after the customer approves the quote
public record AiBookingConfirmRequestDto(
        @NotNull(message = "Room is required")
        Long roomId,

        @NotNull(message = "Check-in is required")
        LocalDate checkIn,

        @NotNull(message = "Check-out is required")
        LocalDate checkOut
) {
}