package be.kdg.prog3.hotels.web;

import be.kdg.prog3.hotels.business.RoomService;
import be.kdg.prog3.hotels.data.DataFactory;
import be.kdg.prog3.hotels.domain.Room;
import be.kdg.prog3.hotels.domain.RoomType;
import be.kdg.prog3.hotels.viewmodel.RoomForm;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

// Controller for handling all requests related to Rooms
@Controller
@RequestMapping("/rooms")   // Base URL path for this controller
public class RoomController {
    // Logger for printing debug information in console or log file
    private static final Logger log = LoggerFactory.getLogger(RoomController.class);
    private final RoomService roomService;   // Injecting RoomService to connect to business logic

    // Constructor injection (Spring automatically provides the service)
    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    /* This method handles GET requests to "/rooms"
    It can show all rooms, or filter them by type, sea view, or max price */
    @GetMapping
    public String list(@RequestParam(name = "type", required = false) String type,
                       @RequestParam(name = "sea", required = false) Boolean sea,
                       @RequestParam(name = "maxPrice", required = false) Double maxPrice,
                       Model model) {

        // Convert incoming query parameters to Optional values
        Optional<RoomType> t = (type == null || type.isBlank())
                ? Optional.empty()
                : Optional.of(RoomType.valueOf(type.toUpperCase()));
        Optional<Boolean> v = Optional.ofNullable(sea);
        Optional<Double> p = Optional.ofNullable(maxPrice);

        // Debug log for filter inputs
        log.debug("Listing rooms with filters type={}, sea={}, maxPrice={}", type, sea, maxPrice);

        // Add filtered results and other attributes to Model (to display on HTML)
        model.addAttribute("rooms", roomService.findRooms(t, v, p));
        model.addAttribute("types", RoomType.values());
        model.addAttribute("selType", type);
        model.addAttribute("selSea", sea);
        model.addAttribute("selPrice", maxPrice);

        return "rooms";   //Return Thymeleaf page name (rooms.html)
    }

    // Add Room Form
    // Shows the form when clicks “Add Room”
    @GetMapping("/add")
    public String addForm(Model model) {
        // Empty Room object to fill form fields using RoomForm class
        model.addAttribute("roomForm", new RoomForm());
        // Dropdown list for RoomType enum
        model.addAttribute("types", RoomType.values());
        return "add-room";  // Return template add-room.html

    }

    // for handling POST request when submits the “Add Room” form
    @PostMapping("/add")
    public String addSubmit(@ModelAttribute("roomForm") @Valid RoomForm roomForm,
                            BindingResult bindingResult,
                            Model model) {

        // if there are validation errors, reload the same page
        if (bindingResult.hasErrors()) {
            log.debug("Validation errors while adding room: {}", bindingResult.getAllErrors());
            model.addAttribute("types", RoomType.values());
            return "add-room";
        }

        // Convert ViewModel to Domain object manually
        var room = new Room();
        room.setNumber(roomForm.getNumber());
        room.setType(roomForm.getType());
        room.setPricePerNight(roomForm.getPricePerNight());
        room.setSeaView(roomForm.isSeaView());
        room.setPhotoUrl(roomForm.getPhotoUrl());

        // Log and save new room data using service layer
        log.debug("Creating new room: {}", room);
        roomService.createdRoom(room);
        return "redirect:/rooms";   // Redirect back to list of rooms after successful submission
    }

    // show details for one specific room by its room number
    @GetMapping("/{number}")
    public String showRoomDetails(@PathVariable int number, Model model) {

        // Find the room that matches the given room number
        var room = DataFactory.rooms.stream()
                .filter(r -> r.getNumber() == number)
                .findFirst()
                .orElse(null);

        // If no room found, redirect back to the rooms list
        if (room == null) {
            return "redirect:/rooms";
        }

        // Get the list of guests who booked this room (many-to-many relationship)
        var guests = DataFactory.guests.stream()
                .filter(g -> g.getRooms().contains(room))
                .toList();

        // Add room and its related guests to the model so the view can display them
        model.addAttribute("room", room);
        model.addAttribute("guests", guests);

        return "room-detail";
    }
}