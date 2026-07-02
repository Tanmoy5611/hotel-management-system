package be.kdg.prog5.hotels.webapi.dto.ai;

import jakarta.validation.constraints.FutureOrPresent;

import java.time.LocalDate;

// Booking quote request from the AI chatbot
public record AiBookingQuoteRequestDto(
        // Preferred because room ids are unique
        Long roomId,

        // Fallback for typed messages like book room 533
        Integer roomNumber,

        // First night of the requested stay
        @FutureOrPresent(message = "Check-in cannot be in the past")
        LocalDate checkIn,

        // Checkout date, must be after check-in in the service
        LocalDate checkOut
) {
}