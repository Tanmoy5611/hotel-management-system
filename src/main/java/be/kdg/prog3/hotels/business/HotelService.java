package be.kdg.prog3.hotels.business;

import be.kdg.prog3.hotels.domain.Hotel;
import java.util.List;


    // Interface for business logic related to hotels
public interface HotelService {
    List<Hotel> getAllHotels();   // Returns all hotels

    // Returns hotels filtered by minimum stars and opened date
    List<Hotel> getHotelsByMinStarsAndDate(int minStars, String dateIn);

    // Creates a new hotel and saves it to the database
    Hotel createdHotel(Hotel hotel);

    // find hotel by id
    Hotel getHotelById(String id);

    void deleteHotel(String id);

}
