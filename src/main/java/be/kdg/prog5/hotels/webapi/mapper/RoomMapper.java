package be.kdg.prog5.hotels.webapi.mapper;

import be.kdg.prog5.hotels.domain.Room;
import be.kdg.prog5.hotels.webapi.dto.RoomDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoomMapper {

    @Mapping(source = "hotel.name", target = "hotelName")
    RoomDto toDto(Room room);
}