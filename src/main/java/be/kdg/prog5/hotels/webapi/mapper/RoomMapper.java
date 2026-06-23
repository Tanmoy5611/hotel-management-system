package be.kdg.prog5.hotels.webapi.mapper;

import be.kdg.prog5.hotels.domain.Room;
import be.kdg.prog5.hotels.webapi.dto.RoomDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
// Maps room data for API responses without returning a Room entity
public interface RoomMapper {

    @Mapping(source = "hotel.name", target = "hotelName")
    @Mapping(source = "hotel.hotelId", target = "hotelId")
    @Mapping(source = "type", target = "type")
    RoomDto toDto(Room room);
}