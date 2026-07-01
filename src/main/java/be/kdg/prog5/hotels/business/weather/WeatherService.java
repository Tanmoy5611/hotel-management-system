package be.kdg.prog5.hotels.business.weather;

public interface WeatherService {
    WeatherReport getCurrentWeather(double latitude, double longitude, Double accuracyMeters, String language);
}