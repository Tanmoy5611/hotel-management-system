package be.kdg.prog5.hotels.webapi.dto.ai;

import java.math.BigDecimal;
import java.time.LocalDate;

// Customer booking row shown inside the chatbot cancel flow
public record AiBookingSummaryDto(
        Long stayId,
        Long roomId,
        int roomNumber,
        String hotelName,
        String city,
        LocalDate checkIn,
        LocalDate checkOut,
        BigDecimal finalPrice
) {
}