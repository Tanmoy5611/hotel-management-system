package be.kdg.prog3.hotels.presentation;

import be.kdg.prog3.hotels.business.HotelService;
import be.kdg.prog3.hotels.business.RoomService;
import be.kdg.prog3.hotels.domain.RoomType;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Optional;

// Connects the MenuView (UI) with HotelService and RoomService (business logic)
@Component
public class MenuPresenter implements CommandLineRunner {
    private final HotelService hotelService;
    private final RoomService roomService;
    private final MenuView view;

    // Constructor injection of services and view
    public MenuPresenter(HotelService hotelService, RoomService roomService, MenuView view) {
        this.hotelService = hotelService;
        this.roomService = roomService;
        this.view = view;

    }

    // runs automatically when Spring Boot starts
    @Override
    public void run(String... args) {

        while (true) {
            view.showMenu();
            String ch = view.readLine();

            // Menu options based on user input
            switch (ch) {
                case "0" -> {                      // Exit program
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


    /// Handles option 2: filter hotels by stars and date
    private void hotelsByMinStarsAndDate() {
        view.print("Minimum stars (1-5): ");
        int minStars = Integer.parseInt(view.readLine());

        view.print("Opened after (yyyy-mm-dd) or empty: ");
        String dateIn = view.readLine().trim();

        view.printHotels(hotelService.getHotelsByMinStarsAndDate(minStars, dateIn));
    }

    /// Handles option 4: filter rooms by type, sea view, and price
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

    // Helper method to safely parse double input
    private Optional<Double> safeDouble(String in) {
        try {
            return Optional.of(Double.parseDouble(in));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}