package be.kdg.prog3.hotels.data;

import be.kdg.prog3.hotels.domain.Hotel;
import java.util.List;

public interface HotelRepository {
    List<Hotel> findAll();

}
