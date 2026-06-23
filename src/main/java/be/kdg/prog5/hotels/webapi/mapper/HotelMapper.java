package be.kdg.prog5.hotels.webapi.mapper;

import be.kdg.prog5.hotels.domain.Hotel;
import be.kdg.prog5.hotels.webapi.dto.HotelDto;
import org.springframework.stereotype.Component;

@Component
// Converts Hotel entities to safe API response data
public class HotelMapper {

    public HotelDto toDto(Hotel hotel) {
        return new HotelDto(
                hotel.getHotelId(),
                hotel.getName(),
                hotel.getCity(),
                hotel.getCountry(),
                hotel.getOpenedOn(),
                hotel.getStars(),
                hotel.hasSpa(),
                hotel.getImageUrl(),
                hotel.getDescription()
        );
    }
}
