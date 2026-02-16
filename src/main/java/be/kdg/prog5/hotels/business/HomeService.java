package be.kdg.prog5.hotels.business;

import be.kdg.prog5.hotels.domain.Hotel;
import be.kdg.prog5.hotels.domain.Room;

import java.util.List;

// orchestrator or facade service
public interface HomeService {

    List<Hotel> getFeaturedHotels();

    List<Hotel> getBeachSpaHotels();

    List<Hotel> getCityHotels();

    List<Room> getBestValueRooms();

    List<Room> getPremiumRooms();

    List<Room> getTopPickedRooms();
}