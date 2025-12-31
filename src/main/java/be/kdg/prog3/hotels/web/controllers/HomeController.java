package be.kdg.prog3.hotels.web.controllers;

import be.kdg.prog3.hotels.business.GuestService;
import be.kdg.prog3.hotels.domain.Hotel;
import be.kdg.prog3.hotels.domain.Room;
import be.kdg.prog3.hotels.business.HotelService;
import be.kdg.prog3.hotels.business.RoomService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    private final HotelService hotelService;
    private final RoomService roomService;
    private final GuestService guestService;

    public HomeController(HotelService hotelService, RoomService roomService, GuestService guestService) {
        this.hotelService = hotelService;
        this.roomService = roomService;
        this.guestService = guestService;
    }

    @GetMapping("/home")
    public String home(Model model) {

        // load all hotels
        List<Hotel> allHotels = hotelService.getAllHotels();

        // Feature hotels: highest stars first
        List<Hotel> featuredHotels = allHotels.stream()
                .sorted(Comparator.comparing(Hotel::getStars).reversed())
                .limit(4)
                .collect(Collectors.toList());

        // Beach & Spa: hotels with SPA
        List<Hotel> beachSpaHotels = allHotels.stream()
                .filter(Hotel::isHasSpa)
                .limit(4)
                .collect(Collectors.toList());

        // popular cities: newest (opened after 2000)
        List<Hotel> cityHotels = allHotels.stream()
                .filter(h -> h.getOpenedOn().getYear() >= 2000)
                .limit(4)
                .collect(Collectors.toList());

        // Best value rooms (4 cheapest rooms)
        List<Room> bestValueRooms = roomService.getAllRooms().stream()
                .sorted(Comparator.comparing(Room::getPricePerNight))
                .limit(4)
                .collect(Collectors.toList());

        // premium rooms: most expensive first
        List<Room> premiumRooms = roomService.getAllRooms().stream()
                .sorted(Comparator.comparing(Room::getPricePerNight).reversed())
                .limit(4)
                .collect(Collectors.toList());

        // TOP PICKS: sorted by number of guests
        List<Room> topPickedRooms = roomService.getAllRooms().stream()
                .sorted(Comparator.comparingInt(
                        (Room r) -> guestService.getGuestsByRoom(r.getNumber()).size()
                ).reversed())
                .limit(4)
                .toList();

        // send to view
        model.addAttribute("featuredHotels", featuredHotels);
        model.addAttribute("beachSpaHotels", beachSpaHotels);
        model.addAttribute("cityHotels", cityHotels);
        model.addAttribute("bestValueRooms", bestValueRooms);
        model.addAttribute("premiumRooms", premiumRooms);
        model.addAttribute("topPickedRooms", topPickedRooms);

        return "home";
    }
}