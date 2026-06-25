package be.kdg.prog5.hotels.web.controllers;

import be.kdg.prog5.hotels.business.room.RoomService;
import be.kdg.prog5.hotels.business.room.SearchResults;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
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

        var results = roomService.searchRoomsForPage(query, roomTypeStr, checkIn, checkOut);
        if (results.emptySearch()) {
            return "redirect:/rooms";
        }

        addSearchResults(model, results);

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
        addSearchResults(model, roomService.emptySearchResults(query, roomType, checkIn, checkOut));
        return "search-results";
    }

    private void addSearchResults(Model model, SearchResults results) {
        model.addAttribute("rooms", results.rooms());
        model.addAttribute("query", results.query());
        model.addAttribute("selectedRoomType", results.roomType());
        model.addAttribute("checkIn", results.checkIn());
        model.addAttribute("checkOut", results.checkOut());
        model.addAttribute("types", results.types());
    }
}