package be.kdg.prog3.hotels.web.converters;
import be.kdg.prog3.hotels.domain.RoomType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Custom converter that lets Spring automatically convert
 * form text values like "SINGLE" - RoomType.SINGLE.
 */

@Component
public class StringToRoomTypeConverter implements Converter<String, RoomType> {

    @Override
    public RoomType convert(String source) {
        if (source == null || source.isBlank()) {
            return null;
        }
        try {
            // convert text (case-insensitive) to RoomType enum
            return RoomType.valueOf(source.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            // if user type or form sent a invalid value, return null
            return null;
        }
    }
}
