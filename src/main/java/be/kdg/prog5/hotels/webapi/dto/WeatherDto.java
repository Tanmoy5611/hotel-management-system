package be.kdg.prog5.hotels.webapi.dto;

public record WeatherDto(
        String location,
        String searchLocation,
        double temperatureCelsius,
        int relativeHumidity,
        double windSpeedKmh,
        int weatherCode,
        String condition,
        String provider,
        String observedAt,
        Double accuracyMeters
) {
}