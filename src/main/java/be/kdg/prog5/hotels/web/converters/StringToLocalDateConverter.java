package be.kdg.prog5.hotels.web.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

// Custom converter that lets Spring automatically turn String to LocalDate
@Component
public class StringToLocalDateConverter implements Converter<String, LocalDate> {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public LocalDate convert(String source) {
        if (source == null || source.isBlank()) return null;
        try {
            return LocalDate.parse(source.trim(), FORMATTER);
        } catch (DateTimeParseException e) {
            // Invalid date format - let validation handle it
            return null;
        }
    }
}