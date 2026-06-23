package be.kdg.prog5.hotels.web.controllers;

import be.kdg.prog5.hotels.business.RoomService;
import be.kdg.prog5.hotels.domain.Room;
import be.kdg.prog5.hotels.domain.RoomType;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

// Handles the public room search page: reads request parameters,
// calls the room service, and sends the results to the Thymeleaf view
@Controller
public class SearchController {

    private final RoomService roomService;

    public SearchController(RoomService roomService) {
        this.roomService = roomService;
    }

    // Week 10 Client search endpoint: returns rooms matching the optional query, room type, check-in and check-out dates
    @GetMapping("/search")
    public String searchRooms(
            @RequestParam(name = "q", required = false) String query,
            @RequestParam(name = "roomType", required = false) String roomTypeStr,
            @RequestParam(name = "checkIn", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam(name = "checkOut", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
            Model model) {

        if ((query == null || query.isBlank())
                && (roomTypeStr == null || roomTypeStr.isBlank())
                && checkIn == null
                && checkOut == null) {
            return "redirect:/rooms";
        }

        List<Room> rooms = roomService.searchAvailableRooms(query, roomTypeStr, checkIn, checkOut);

        model.addAttribute("rooms", rooms);
        model.addAttribute("query", query);
        model.addAttribute("selectedRoomType", roomTypeStr);
        model.addAttribute("checkIn", checkIn);
        model.addAttribute("checkOut", checkOut);
        model.addAttribute("types", RoomType.values());

        return "search-results";
    }

    // Week 10 Client search endpoint: handles exceptions thrown by the room service
    @ExceptionHandler(IllegalArgumentException.class)
    public String handleBadSearch(IllegalArgumentException ex,
                                  @RequestParam(name = "q", required = false) String query,
                                  @RequestParam(name = "roomType", required = false) String roomType,
                                  @RequestParam(name = "checkIn", required = false)
                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
                                  @RequestParam(name = "checkOut", required = false)
                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
                                  Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("rooms", List.of());
        model.addAttribute("query", query);
        model.addAttribute("selectedRoomType", roomType);
        model.addAttribute("checkIn", checkIn);
        model.addAttribute("checkOut", checkOut);
        model.addAttribute("types", RoomType.values());
        return "search-results";
    }
}