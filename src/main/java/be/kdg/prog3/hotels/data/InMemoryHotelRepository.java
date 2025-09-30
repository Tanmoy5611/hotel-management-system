package be.kdg.prog3.hotels.data;

import be.kdg.prog3.hotels.domain.Hotel;
import be.kdg.prog3.hotels.domain.Room;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Component;

@Component
public class InMemoryHotelRepository implements HotelRepository {
    @Override
    public List<Hotel> findAll() {
        return DataFactory.rooms.stream()
                .map(Room::getHotel)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

    }
}
