package be.kdg.prog5.hotels.web.controllers;

import be.kdg.prog5.hotels.business.RoomService;
import be.kdg.prog5.hotels.domain.Room;
import be.kdg.prog5.hotels.domain.RoomType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

// Handles the public room search page: reads request parameters,
// calls the room service, and sends the results to the Thymeleaf view
@Controller
public class SearchController {

    private static final Logger log = LoggerFactory.getLogger(SearchController.class);

    private final RoomService roomService;

    public SearchController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping("/search")
    public String searchRooms(
            @RequestParam(name = "q", required = false) String query,
            @RequestParam(name = "roomType", required = false) String roomTypeStr,
            @RequestParam(name = "checkIn", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam(name = "checkOut", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
            Model model) {

        log.debug("Search request: q={}, roomType={}, checkIn={}, checkOut={}",
                query, roomTypeStr, checkIn, checkOut);

        // Convert String -> Enum safely (prevents crash when empty "")
        RoomType roomType = null;
        if (roomTypeStr != null && !roomTypeStr.isBlank()) {
            roomType = RoomType.valueOf(roomTypeStr);
        }

        // If user submits empty search, redirect to all rooms
        // (better UX + avoids unnecessary DB calls)
        if ((query == null || query.isBlank()) && roomType == null && checkIn == null && checkOut == null) {
            return "redirect:/rooms"; // or /hotels
        }

        List<Room> rooms;
        String errorMessage = null;

        try {
            // Call service layer (business logic)
            rooms = roomService.searchAvailableRooms(
                    query, roomType, checkIn, checkOut);
        } catch (IllegalArgumentException ex) {
            // Handle invalid input (wrong dates)
            log.warn("Invalid search input: {}", ex.getMessage());
            
            rooms = List.of();
            errorMessage = ex.getMessage();
        }

        model.addAttribute("rooms", rooms);
        model.addAttribute("query", query);
        model.addAttribute("selectedRoomType", roomType);
        model.addAttribute("checkIn", checkIn);
        model.addAttribute("checkOut", checkOut);
        model.addAttribute("types", RoomType.values());
        model.addAttribute("errorMessage", errorMessage);

        return "search-results";
    }
}
