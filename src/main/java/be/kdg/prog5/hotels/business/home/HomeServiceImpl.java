package be.kdg.prog5.hotels.business.home;

import be.kdg.prog5.hotels.business.hotel.HotelService;
import be.kdg.prog5.hotels.business.room.RoomService;
import be.kdg.prog5.hotels.domain.Hotel;
import be.kdg.prog5.hotels.domain.Room;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

// For Home page dashboard
@Service
@Transactional(readOnly = true)
public class HomeServiceImpl implements HomeService {

    private static final Logger log =
            LoggerFactory.getLogger(HomeServiceImpl.class);

    private final HotelService hotelService;
    private final RoomService roomService;

    public HomeServiceImpl(HotelService hotelService,
                           RoomService roomService) {
        this.hotelService = hotelService;
        this.roomService = roomService;
    }

    @Override
    public HomePage getHomePage() {
        return new HomePage(
                getFeaturedHotels(),
                getBeachSpaHotels(),
                getCityHotels(),
                getBestValueRooms(),
                getPremiumRooms(),
                getTopPickedRooms()
        );
    }

    // Returns top‑rated hotels, limited to four
    @Override
    public List<Hotel> getFeaturedHotels() {
        log.debug("Getting featured hotels");

        return hotelService.getFeaturedHotels();
    }

    // Returns hotels with SPA facilities, limited to four
    @Override
    public List<Hotel> getBeachSpaHotels() {
        log.debug("Getting hotels with a beach spa");

        return hotelService.getBeachSpaHotels();
    }

    // Returns hotels in the city after 2000, limited to four
    @Override
    public List<Hotel> getCityHotels() {
        log.debug("Getting hotels in the city");

        return hotelService.getCityHotels(LocalDate.of(2000, 1, 1));
    }

    // Returns four lowest‑priced rooms
    @Override
    public List<Room> getBestValueRooms() {
        log.debug("Getting best value rooms");

        return roomService.getBestValueRooms();
    }

    // Returns up to four highest priced rooms
    @Override
    public List<Room> getPremiumRooms() {
        log.debug("Getting premium rooms");

        return roomService.getPremiumRooms();
    }

    // Returns top picked rooms, limited to four
    @Override
    public List<Room> getTopPickedRooms() {
        log.debug("Getting top picked rooms");

        return roomService.getTopPickedRooms();
    }
}