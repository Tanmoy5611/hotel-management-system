package be.kdg.prog5.hotels.webapi.dto.ai;

import java.math.BigDecimal;

// Structured interpretation of the customer's natural-language chat message
public record AiSearchFiltersDto(
        // Destination filter extracted from the message
        String city,

        // Upper price limit extracted from budget phrases
        BigDecimal maxPrice,

        // Room type normalized to the Spring enum names
        String roomType,

        // Optional amenity preferences extracted from text
        Boolean hasSpa,
        Boolean seaView,

        // Minimum stars inferred from luxury-style words
        Integer minStars
) {
}