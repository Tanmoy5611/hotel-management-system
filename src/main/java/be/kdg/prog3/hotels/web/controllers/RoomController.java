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

import java.util.List;
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
    public String list(
            @RequestParam(name = "number", required = false) Integer number,
            @RequestParam(name = "type", required = false) String type,
            @RequestParam(name = "sea", required = false) Boolean sea,
            @RequestParam(name = "maxPrice", required = false) Double maxPrice,
            Model model) {

        // If room number is provided : direct lookup
        if (number != null && number > 0) {
            log.debug("Filtering rooms by number {}", number);

            List<Room> rooms = roomService.getRoomsByNumber(number);

            model.addAttribute("rooms", rooms);
            model.addAttribute("types", RoomType.values());
            model.addAttribute("selType", type);
            model.addAttribute("selSea", sea);
            model.addAttribute("selPrice", maxPrice);

            return "rooms";
        }

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

        return "rooms";   // Return Thymeleaf page (rooms.html)
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
                roomForm.getPhotoUrl(),
                roomForm.getDescription()
        );

        room.setHotel(hotel);
        room.setDescription(roomForm.getDescription());


        // Log and save new room data using service layer
        log.debug("Creating new room: {}", room);
        roomService.createRoom(room);

        return "redirect:/rooms";   // Redirect back to list of rooms after successful submission
    }

    // show details for one specific room by its room id
    @GetMapping("/{roomId}")
    public String showRoomDetails(@PathVariable Long roomId, Model model) {

        // Find the room that matches the given room number
        Room room = roomService.getRoomById(roomId);

        // If no room found, redirect back to the rooms list
        if (room == null) {
            log.error("Room {} not found!", roomId);

            return "redirect:/rooms";
        }

        // Get the list of guests who booked this room (many-to-many relationship)
        var guests = guestService.getGuestsByRoom(roomId);

        // Add room and its related guests to the model so the view can display them
        model.addAttribute("room", room);
        model.addAttribute("guests", guests);

        return "room-detail";
    }

    // delete a room by id
    @PostMapping("/{roomId}/delete")
    public String deleteRoom(@PathVariable Long roomId) {
        log.debug("Deleting room {}", roomId);
        roomService.deleteRoom(roomId);
        return "redirect:/rooms";
    }

    ///  Business exception handler
    @ExceptionHandler(RoomNotFoundException.class)
    public String handleRoomNotFound(RoomNotFoundException ex, Model model) {

        log.warn("Business error: {}", ex.getMessage());

        model.addAttribute("errorMessage", ex.getMessage());

        return "error/general-error";
    }



    /// Room description
    @GetMapping("/{roomId}/edit-description")
    public String editRoomDescriptionForm(@PathVariable Long roomId, Model model) {

        Room room = roomService.getRoomById(roomId);

        model.addAttribute("room", room);

        return "edit-room-description"; // Thymeleaf page
    }

    @PostMapping("/{roomId}/edit-description")
    public String updateRoomDescription(@PathVariable Long roomId,
                                        @RequestParam String description) {

        roomService.updateRoomDescription(roomId, description);

        return "redirect:/rooms/" + roomId;
    }
}