package be.kdg.prog5.hotels.webapi.dto.ai;

import java.math.BigDecimal;
import java.time.LocalDate;

// Availability and price summary shown before the chatbot confirms booking
public record AiBookingQuoteResponseDto(
        boolean available,
        String message,
        Long roomId,
        int roomNumber,
        String hotelName,
        String city,
        String roomType,
        LocalDate checkIn,
        LocalDate checkOut,
        long nights,
        BigDecimal pricePerNight,
        BigDecimal totalPrice,
        BigDecimal discountPercentage,
        BigDecimal finalPrice
) {
}