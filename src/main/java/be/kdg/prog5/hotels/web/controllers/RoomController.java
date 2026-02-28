package be.kdg.prog5.hotels.web.controllers;
import be.kdg.prog5.hotels.business.GuestService;
import be.kdg.prog5.hotels.business.HotelService;
import be.kdg.prog5.hotels.business.RoomService;
import be.kdg.prog5.hotels.business.exceptions.BookingException;
import be.kdg.prog5.hotels.business.exceptions.RoomNotFoundException;
import be.kdg.prog5.hotels.domain.Room;
import be.kdg.prog5.hotels.domain.RoomType;
import be.kdg.prog5.hotels.viewmodel.RoomForm;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

// Controller for handling all requests related to Rooms
@Controller
@RequestMapping("/rooms")   // Base URL path for this controller
public class RoomController {
    // Logger for printing debug information in console or log file
    private static final Logger log = LoggerFactory.getLogger(RoomController.class);

    private final RoomService roomService;   // Injecting RoomService to connect to business logic
    private final HotelService hotelService;
    private final GuestService guestService;

    // Constructor injection (Spring automatically provides the service)
    public RoomController(RoomService roomService, HotelService hotelService, GuestService guestService) {
        this.roomService = roomService;
        this.hotelService = hotelService;
        this.guestService = guestService;
    }

    // shows all rooms, or filter them by type, sea view, or max price in the room page filtering
    @GetMapping
    public String list(
            @RequestParam(name = "number", required = false) Integer roomNumber,
            @RequestParam(name = "type", required = false) String type,
            @RequestParam(name = "sea", required = false) Boolean sea,
            @RequestParam(name = "maxPrice", required = false) BigDecimal maxPrice,
            Model model) {

        // If a room number is provided : direct lookup
        if (roomNumber != null && roomNumber > 0) {
            log.debug("Filtering rooms by number {}", roomNumber);

            List<Room> rooms = roomService.getRoomsByNumber(roomNumber);

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
        Optional<BigDecimal> p = Optional.ofNullable(maxPrice);

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

    /// Add Room Form
    // Shows the form when clicks “Add Room”
    @GetMapping("/add")
    public String addForm(Model model) {
        log.debug("Loading add room form");

        // Empty Room object to fill form fields using RoomForm class
        model.addAttribute("roomForm", new RoomForm());
        // Dropdown list for RoomType enum
        model.addAttribute("types", RoomType.values());
        model.addAttribute("hotels", hotelService.getAllHotels());

        return "add-room";  // Return add-room page
    }

    // for handling POST request when submits the “Add Room” form
    @PostMapping("/add")
    public String addSubmit(@Valid @ModelAttribute("roomForm") RoomForm roomForm,
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
        var hotel = hotelService.getHotelByHotelId(roomForm.getHotelId());
        if (hotel == null) {
            bindingResult.rejectValue("hotelId", "hotel.invalid", "Invalid hotel");
            model.addAttribute("types", RoomType.values());
            model.addAttribute("hotels", hotelService.getAllHotels());
            return "add-room";
        }

        Room room = new Room(
                roomForm.getNumber(),
                roomForm.getType(),
                roomForm.getPricePerNight(),
                roomForm.isSeaView(),
                roomForm.getPhotoUrl(),
                roomForm.getDescription()
        );

        room.setHotel(hotel);

        // room.setDescription(roomForm.getDescription());

        // Log and save new room data using the service layer
        log.debug("Creating new room: {}", room);
        roomService.createRoom(room, roomForm.getHotelId());


        return "redirect:/rooms";   // Redirect back to list of rooms after successful submission
    }

    // show Room details for one specific room by its room id
    @GetMapping("/{roomId}")
    public String showRoomDetails(@PathVariable Long roomId, Model model) {
        log.debug("Loading room details for room {}", roomId);

        // Find the room that matches the given room number
        // Service loads Room aggregate using JOIN FETCH
        Room room = roomService.getRoomById(roomId);

        // Add a room and its related guests to the model so the view can display them
        model.addAttribute("room", room);
        var sortedStays = room.getStays().stream()
                .sorted((s1, s2) -> s1.getCheckInDate().compareTo(s2.getCheckInDate()))
                .toList();

        model.addAttribute("guests", sortedStays);
        model.addAttribute("today", LocalDate.now());

        return "room-detail";
    }

    // Show Booking Page
    @GetMapping("/{roomId}/book")
    public String showBookingPage(@PathVariable Long roomId,
                                  Model model) {
        log.debug("Loading booking form for room {}", roomId);

        Room room = roomService.getRoomById(roomId);

        model.addAttribute("room", room);
        model.addAttribute("allGuests", guestService.getAllGuests()); // Only loaded when needed

        return "book-room";
    }

    @PostMapping("/{roomId}/book")
    public String processBooking(@PathVariable Long roomId,
                                 @RequestParam Long guestId,
                                 @RequestParam LocalDate checkIn,
                                 @RequestParam LocalDate checkOut,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {

        log.debug("Processing booking for room {} with guest {}", roomId, guestId);

        // Attempts booking; redirects on success; returns form on failure
        try {
            roomService.bookRoom(roomId, guestId, checkIn, checkOut);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "booking.success"
            );

            return "redirect:/rooms/" + roomId;

        } catch (BookingException ex) {

            Room room = roomService.getRoomById(roomId);

            model.addAttribute("room", room);
            model.addAttribute("allGuests", guestService.getAllGuests());

            // keep previously entered values
            model.addAttribute("selectedGuestId", guestId);
            model.addAttribute("selectedCheckIn", checkIn);
            model.addAttribute("selectedCheckOut", checkOut);

            model.addAttribute("errorCode", ex.getCode());

            return "book-room";
        }
    }

    /// delete a room by its id
    @PostMapping("/{roomId}/delete")
    public String deleteRoom(@PathVariable Long roomId) {
        log.debug("Deleting room {}", roomId);

        roomService.deleteRoom(roomId);
        return "redirect:/rooms";
    }

    /// Room description
    @GetMapping("/{roomId}/edit-description")
    public String editRoomDescriptionForm(@PathVariable Long roomId,
                                          Model model) {
        log.debug("Loading edit room description form for room {}", roomId);

        Room room = roomService.getRoomById(roomId);

        model.addAttribute("room", room);

        return "edit-room-description"; // Thymeleaf page
    }

    @PostMapping("/{roomId}/edit-description")
    public String updateRoomDescription(@PathVariable Long roomId,
                                        @RequestParam String description) {
        log.debug("Updating room description for room {}", roomId);

        roomService.updateRoomDescription(roomId, description);

        return "redirect:/rooms/" + roomId;
    }

    ///  Business exception handler
    @ExceptionHandler(RoomNotFoundException.class)
    public String handleRoomNotFound(RoomNotFoundException ex,
                                     Model model) {
        log.warn("Business error: {}", ex.getMessage());

        model.addAttribute("errorMessage", ex.getMessage());

        return "error/general-error";
    }
}