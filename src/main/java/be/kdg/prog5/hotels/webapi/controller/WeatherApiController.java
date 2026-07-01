package be.kdg.prog5.hotels.webapi.controller;

import be.kdg.prog5.hotels.business.weather.WeatherReport;
import be.kdg.prog5.hotels.business.weather.WeatherService;
import be.kdg.prog5.hotels.webapi.dto.WeatherDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/weather")
public class WeatherApiController {
    private final WeatherService weatherService;

    public WeatherApiController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping(value = "/current", produces = MediaType.APPLICATION_JSON_VALUE)
    // Reads browser coordinates and returns weather data for the home page widget
    public ResponseEntity<WeatherDto> getCurrentWeather(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam(required = false) Double accuracyMeters,
            HttpServletRequest request) {

        WeatherReport report = weatherService.getCurrentWeather(
                latitude,
                longitude,
                accuracyMeters,
                request.getLocale().toLanguageTag()
        );

        return ResponseEntity.ok(toDto(report));
    }

    // Keeps the API response separate from the business model
    private WeatherDto toDto(WeatherReport report) {
        return new WeatherDto(
                report.location(),
                report.searchLocation(),
                report.temperatureCelsius(),
                report.relativeHumidity(),
                report.windSpeedKmh(),
                report.weatherCode(),
                report.condition(),
                report.provider(),
                report.observedAt(),
                report.accuracyMeters()
        );
    }
}