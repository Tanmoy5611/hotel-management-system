package be.kdg.prog3.hotels.data;

import be.kdg.prog3.hotels.domain.Hotel;
import java.util.List;

 // Repository interface for Hotel entity
public interface HotelRepository {
    List<Hotel> findAll();    // for retrieving all store hotels
    Hotel save(Hotel hotel);  // saves a new hotel to the data source

}
