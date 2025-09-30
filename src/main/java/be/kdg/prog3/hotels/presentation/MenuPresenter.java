package be.kdg.prog3.hotels.presentation;

import be.kdg.prog3.hotels.business.HotelService;
import be.kdg.prog3.hotels.business.RoomService;
import be.kdg.prog3.hotels.domain.RoomType;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class MenuPresenter implements CommandLineRunner {
    private final HotelService hotelService;
    private final RoomService roomService;
    private final MenuView view;

    public MenuPresenter(HotelService hotelService, RoomService roomService, MenuView view) {
        this.hotelService = hotelService;
        this.roomService = roomService;
        this.view = view;

    }

    @Override
    public void run(String... args) {

        // this will start automatically when Spring Boot launches
        while (true) {
            view.showMenu();
            String ch = view.readLine();
            switch (ch) {
                case "0" -> {
                    view.print("Bye!!");
                    return;
                }
                case "1" -> view.printHotels(hotelService.getAllHotels());
                case "2" -> hotelsByMinStarsAndDate();
                case "3" -> view.printRooms(roomService.getAllRooms());
                case "4" -> roomsWithOptionalFilters();
                default -> view.print("Invalid choice.");
            }
            view.print("");
        }
    }

    private void hotelsByMinStarsAndDate() {
        view.print("Minimum stars (1-5): ");
        int minStars = Integer.parseInt(view.readLine());

        view.print("Opened after (yyyy-mm-dd) or empty: ");
        String dateIn = view.readLine().trim();

        view.printHotels(hotelService.getHotelsByMinStarsAndDate(minStars, dateIn));
    }

    private void roomsWithOptionalFilters() {
        view.print("Type (SINGLE/DOUBLE/SUITE) or empty: ");
        String t = view.readLine().trim();
        Optional<RoomType> type = t.isEmpty() ? Optional.empty() : Optional.of(RoomType.valueOf(t.toUpperCase()));

        view.print("Sea view? (true/false) or empty: ");
        String s = view.readLine().trim();
        Optional<Boolean> sea = s.isEmpty() ? Optional.empty() : Optional.of(Boolean.parseBoolean(s));

        view.print("Max price or Empty: ");
        String p = view.readLine().trim();
        Optional<Double> max = p.isEmpty() ? Optional.empty() : safeDouble(p);

        view.printRooms(roomService.findRooms(type, sea, max));
    }

    private Optional<Double> safeDouble(String in) {
        try {
            return Optional.of(Double.parseDouble(in));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}