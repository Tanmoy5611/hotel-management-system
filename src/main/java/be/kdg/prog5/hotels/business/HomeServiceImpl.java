package be.kdg.prog5.hotels.business;

import be.kdg.prog5.hotels.domain.Hotel;
import be.kdg.prog5.hotels.domain.Room;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HomeServiceImpl implements HomeService {

    private static final Logger log =
            LoggerFactory.getLogger(HomeServiceImpl.class);

    private final HotelService hotelService;
    private final RoomService roomService;
    private final GuestService guestService;

    public HomeServiceImpl(HotelService hotelService,
                           RoomService roomService,
                           GuestService guestService) {
        this.hotelService = hotelService;
        this.roomService = roomService;
        this.guestService = guestService;
    }

    // Returns top‑rated hotels, limited to four
    @Override
    public List<Hotel> getFeaturedHotels() {
        log.debug("Getting featured hotels");

        return hotelService.getAllHotels().stream()
                .sorted(Comparator.comparing(Hotel::getStars).reversed())
                .limit(4)
                .toList();
    }

    // Returns hotels with SPA facilities, limited to four
    @Override
    public List<Hotel> getBeachSpaHotels() {
        log.debug("Getting hotels with a beach spa");

        return hotelService.getAllHotels().stream()
                .filter(Hotel::hasSpa)
                .limit(4)
                .toList();
    }

    // Returns hotels in the city after 2000, limited to four
    @Override
    public List<Hotel> getCityHotels() {
        log.debug("Getting hotels in the city");

        return hotelService.getAllHotels().stream()
                .filter(h ->  h.getOpenedOn() != null &&
                        h.getOpenedOn().getYear() >= 2000)
                .limit(4)
                .toList();
    }

    // Returns four lowest‑priced rooms
    @Override
    public List<Room> getBestValueRooms() {
        log.debug("Getting best value rooms");

        return roomService.getAllRooms().stream()
                .sorted(Comparator.comparing(Room::getPricePerNight))
                .limit(4)
                .toList();
    }

    // Returns up to four highest priced rooms
    @Override
    public List<Room> getPremiumRooms() {
        log.debug("Getting premium rooms");

        return roomService.getAllRooms().stream()
                .sorted(Comparator.comparing(Room::getPricePerNight).reversed())
                .limit(4)
                .toList();
    }

    // Returns top picked rooms, limited to four
    @Override
    public List<Room> getTopPickedRooms() {
        log.debug("Getting top picked rooms");

        return roomService.getAllRooms().stream()
                .sorted(Comparator.comparingInt(
                        (Room r) -> guestService.getGuestsByRoom(r.getId()).size()
                ).reversed())
                .limit(4)
                .toList();
    }
}