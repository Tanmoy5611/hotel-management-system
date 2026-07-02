package be.kdg.prog5.hotels.webapi.dto.ai;

import java.math.BigDecimal;

// Flat ML feature object shared by Spring and Python
public record AiRoomFeatureDto(
        // Room primary key used for linking recommendation cards back to room pages
        Long roomId,

        // Hotel identifier and display data avoid extra lookups in Python
        String hotelId,
        String hotelName,
        int roomNumber,

        // Location and quality signals used by the recommender
        String city,
        int stars,
        boolean hasSpa,

        // Room product features used for both chat filters and vector scoring
        String roomType,
        BigDecimal pricePerNight,
        boolean seaView,

        // Stay-specific features are only present for past bookings
        Integer numberOfNights,
        BigDecimal discountPercentage
) {
}