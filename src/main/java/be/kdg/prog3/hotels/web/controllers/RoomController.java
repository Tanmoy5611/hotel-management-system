package be.kdg.prog3.hotels.web.controllers;

import be.kdg.prog3.hotels.business.GuestService;
import be.kdg.prog3.hotels.business.HotelService;
import be.kdg.prog3.hotels.business.RoomService;
import be.kdg.prog3.hotels.business.exceptions.RoomNotFoundException;
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
    private final GuestService guestService;
    private final HotelService hotelService;

    // Constructor injection (Spring automatically provides the service)
    public RoomController(RoomService roomService, GuestService guestService, HotelService hotelService) {
        this.roomService = roomService;
        this.guestService = guestService;
        this.hotelService = hotelService;
    }

    /* This method handles GET requests to "/rooms"
    It can show all rooms, or filter them by type, sea view, or max price */
    @GetMapping
    public String list(@RequestParam(name = "number", required = false) Integer number,
                       @RequestParam(name = "type", required = false) String type,
                       @RequestParam(name = "sea", required = false) Boolean sea,
                       @RequestParam(name = "maxPrice", required = false) Double maxPrice,
                       Model model) {

        // If room number is provided : direct lookup
        if (number != null) {
            Room room = roomService.getRoomByNumber(number); // throws RoomNotFoundException

            return "redirect:/rooms/" + room.getNumber();
        }

        // Convert incoming query parameters to Optional values
        Optional<RoomType> t = (type == null || type.isBlank())
                ? Optional.empty()
                : Optional.of(RoomType.valueOf(type.toUpperCase()));

        Optional<Boolean> v = Optional.ofNullable(sea);
        Optional<Double> p = Optional.ofNullable(maxPrice);

        // Debug log for filter inputs
        log.debug("Listing rooms with filters number={}, type={}, sea={}, maxPrice={}", number, type, sea, maxPrice);

        // Add filtered results and other attributes to Model (to display on HTML)
        model.addAttribute("rooms", roomService.findRooms(t, v, p));
        model.addAttribute("types", RoomType.values());
        model.addAttribute("selType", type);
        model.addAttribute("selSea", sea);
        model.addAttribute("selPrice", maxPrice);
        model.addAttribute("selNumber", number);

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
        model.addAttribute("hotels", hotelService.getAllHotels());

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
            model.addAttribute("hotels", hotelService.getAllHotels());

            return "add-room";
        }

        // using full constructor because JPA no-args constructor is protected
        var hotel = hotelService.getHotelById(roomForm.getHotelId());

        Room room = new Room(
                roomForm.getNumber(),
                roomForm.getType(),
                roomForm.getPricePerNight(),
                roomForm.isSeaView(),
                roomForm.getPhotoUrl()
        );

        room.setHotel(hotel);

        // Log and save new room data using service layer
        log.debug("Creating new room: {}", room);
        roomService.createdRoom(room);

        return "redirect:/rooms";   // Redirect back to list of rooms after successful submission
    }

    // show details for one specific room by its room number
    @GetMapping("/{number}")
    public String showRoomDetails(@PathVariable int number, Model model) {

        // Find the room that matches the given room number
        var room = roomService.getRoomByNumber(number);

        // If no room found, redirect back to the rooms list
        if (room == null) {
            log.error("Room {} not found!", number);

            return "redirect:/rooms";
        }

        // Get the list of guests who booked this room (many-to-many relationship)
        var guests = guestService.getGuestsByRoom(number);

        // Add room and its related guests to the model so the view can display them
        model.addAttribute("room", room);
        model.addAttribute("guests", guests);

        return "room-detail";
    }

    // delete a room
    @PostMapping("/{number}/delete")
    public String deleteRoom(@PathVariable int number) {
        log.debug("Deleting room {}", number);
        roomService.deleteRoom(number);
        return "redirect:/rooms";
    }

    ///  Business exception handler
    @ExceptionHandler(RoomNotFoundException.class)
    public String handleRoomNotFound(RoomNotFoundException ex, Model model) {

        log.warn("Business error: {}", ex.getMessage());

        model.addAttribute("errorMessage", ex.getMessage());

        return "error/general-error";
    }
}