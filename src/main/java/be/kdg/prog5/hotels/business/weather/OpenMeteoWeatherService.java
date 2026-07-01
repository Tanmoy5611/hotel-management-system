package be.kdg.prog5.hotels.business.weather;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Locale;

@Service
public class OpenMeteoWeatherService implements WeatherService {
    private static final String PROVIDER_NAME = "Open-Meteo";
    private static final String APPLICATION_USER_AGENT = "HotelBooking/1.0";
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(5);

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public OpenMeteoWeatherService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(REQUEST_TIMEOUT)
                .build();
    }

    @Override
    // Coordinates come from the browser and the external providers return live data
    public WeatherReport getCurrentWeather(double latitude, double longitude, Double accuracyMeters, String language) {
        validateCoordinates(latitude, longitude);

        try {
            JsonNode place = fetchNearestPlace(latitude, longitude, language);
            JsonNode currentWeather = fetchCurrentWeather(latitude, longitude);

            return new WeatherReport(
                    getLocationLabel(place),
                    getSearchLocation(place),
                    currentWeather.path("temperature_2m").asDouble(),
                    currentWeather.path("relative_humidity_2m").asInt(),
                    currentWeather.path("wind_speed_10m").asDouble(),
                    currentWeather.path("weather_code").asInt(),
                    getWeatherCondition(currentWeather.path("weather_code").asInt()),
                    PROVIDER_NAME,
                    currentWeather.path("time").asText(),
                    accuracyMeters
            );
        } catch (IOException ex) {
            throw new WeatherServiceException("Weather provider response could not be read");
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new WeatherServiceException("Weather provider request was interrupted");
        }
    }

    // Address lookup is optional so weather still works if geocoding is unavailable
    private JsonNode fetchNearestPlace(double latitude, double longitude, String language) {
        URI uri = UriComponentsBuilder
                .fromUriString("https://nominatim.openstreetmap.org/reverse")
                .queryParam("lat", latitude)
                .queryParam("lon", longitude)
                .queryParam("format", "jsonv2")
                .queryParam("zoom", 14)
                .queryParam("addressdetails", 1)
                .build()
                .toUri();

        try {
            return getJson(uri, language, true);
        } catch (IOException | InterruptedException | WeatherServiceException ex) {
            if (ex instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }

            return null;
        }
    }

    // Open-Meteo provides current temperature, humidity, weather code, and wind speed
    private JsonNode fetchCurrentWeather(double latitude, double longitude)
            throws IOException, InterruptedException {
        URI uri = UriComponentsBuilder
                .fromUriString("https://api.open-meteo.com/v1/forecast")
                .queryParam("latitude", latitude)
                .queryParam("longitude", longitude)
                .queryParam("current", "temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m")
                .queryParam("temperature_unit", "celsius")
                .queryParam("wind_speed_unit", "kmh")
                .queryParam("timezone", "auto")
                .queryParam("forecast_days", 1)
                .build()
                .toUri();

        JsonNode current = getJson(uri, null, false).path("current");

        if (current.isMissingNode() || current.isNull()) {
            throw new WeatherServiceException("Weather provider returned no current conditions");
        }

        return current;
    }

    // Shared HTTP helper for weather and address provider calls
    private JsonNode getJson(URI uri, String language, boolean includeContactHeaders)
            throws IOException, InterruptedException {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder(uri)
                .timeout(REQUEST_TIMEOUT)
                .GET();

        if (includeContactHeaders) {
            requestBuilder
                    .header("User-Agent", APPLICATION_USER_AGENT)
                    .header("Accept-Language", normalizeLanguage(language));
        }

        HttpRequest request = requestBuilder.build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new WeatherServiceException("Weather provider returned status " + response.statusCode());
        }

        return objectMapper.readTree(response.body());
    }

    // Reject impossible coordinates before calling external services
    private void validateCoordinates(double latitude, double longitude) {
        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("Latitude must be between -90 and 90");
        }

        if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("Longitude must be between -180 and 180");
        }
    }

    // External address lookup accepts simple language tags like en, nl, fr
    private String normalizeLanguage(String language) {
        if (language == null || language.isBlank()) {
            return "en";
        }

        return Locale.forLanguageTag(language).getLanguage();
    }

    // Builds a readable label such as Sint-Andries, Antwerp, Belgium
    private String getLocationLabel(JsonNode place) {
        if (place == null) {
            return "Current location";
        }

        JsonNode address = place.path("address");
        String district = firstPresent(
                address.path("quarter").asText(null),
                address.path("suburb").asText(null),
                address.path("neighbourhood").asText(null),
                address.path("borough").asText(null)
        );
        String city = firstPresent(
                address.path("city").asText(null),
                address.path("town").asText(null),
                address.path("village").asText(null),
                address.path("municipality").asText(null),
                address.path("county").asText(null)
        );
        String location = joinLocationParts(district, city, address.path("country").asText(null));

        if (!"Current location".equals(location)) {
            return location;
        }

        return firstPresent(place.path("display_name").asText(null), "Current location");
    }

    // Keeps the search value shorter than the full address label
    private String getSearchLocation(JsonNode place) {
        if (place == null) {
            return "";
        }

        JsonNode address = place.path("address");
        return joinLocationParts(
                firstPresent(
                        address.path("city").asText(null),
                        address.path("town").asText(null),
                        address.path("village").asText(null),
                        address.path("municipality").asText(null),
                        address.path("borough").asText(null),
                        address.path("county").asText(null)
                ),
                address.path("country").asText(null)
        );
    }

    // Returns the first non-empty address field
    private String firstPresent(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }

        return null;
    }

    // Joins address parts and avoids repeated words like Antwerp, Antwerp
    private String joinLocationParts(String... parts) {
        StringBuilder location = new StringBuilder();

        for (String part : parts) {
            if (part == null || part.isBlank()) {
                continue;
            }

            String normalizedPart = part.trim();
            String normalizedLocation = location.toString().toLowerCase(Locale.ROOT);

            if (normalizedLocation.contains(normalizedPart.toLowerCase(Locale.ROOT))) {
                continue;
            }

            if (!location.isEmpty()) {
                location.append(", ");
            }

            location.append(normalizedPart);
        }

        return location.isEmpty() ? "Current location" : location.toString();
    }

    // Converts WMO weather codes into labels the home page can show directly
    private String getWeatherCondition(int weatherCode) {
        return switch (weatherCode) {
            case 0 -> "Clear sky";
            case 1 -> "Mainly clear";
            case 2 -> "Partly cloudy";
            case 3 -> "Overcast";
            case 45 -> "Foggy";
            case 48 -> "Rime fog";
            case 51 -> "Light drizzle";
            case 53 -> "Drizzle";
            case 55 -> "Heavy drizzle";
            case 61 -> "Light rain";
            case 63 -> "Rain";
            case 65 -> "Heavy rain";
            case 71 -> "Light snow";
            case 73 -> "Snow";
            case 75 -> "Heavy snow";
            case 80 -> "Light showers";
            case 81 -> "Showers";
            case 82 -> "Heavy showers";
            case 95 -> "Thunderstorm";
            case 96 -> "Thunderstorm with hail";
            case 99 -> "Severe thunderstorm";
            default -> "Live weather";
        };
    }
}