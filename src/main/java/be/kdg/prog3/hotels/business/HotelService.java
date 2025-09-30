package be.kdg.prog3.hotels.business;

import be.kdg.prog3.hotels.domain.Hotel;
import java.util.List;

public interface HotelService {
    List<Hotel> getAllHotels();
    List<Hotel> getHotelsByMinStarsAndDate(int minStars, String dateIn);
}
