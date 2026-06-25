package be.kdg.prog5.hotels.web.controllers;
import be.kdg.prog5.hotels.business.booking.BookingService;
import be.kdg.prog5.hotels.business.hotel.HotelService;
import be.kdg.prog5.hotels.business.room.RoomService;
import be.kdg.prog5.hotels.business.exceptions.BookingException;
import be.kdg.prog5.hotels.business.exceptions.RoomNotFoundException;
import be.kdg.prog5.hotels.domain.Room;
import be.kdg.prog5.hotels.domain.RoomType;
import be.kdg.prog5.hotels.viewmodel.RoomForm;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

// Controller for handling all requests related to Rooms
@Controller
@RequestMapping("/rooms")   // Base URL path for this controller
public class RoomController {
    // Logger for printing debug information in console or log file
    private static final Logger log = LoggerFactory.getLogger(RoomController.class);

    private final RoomService roomService;   // Injecting RoomService to connect to business logic
    private final HotelService hotelService;
    private final BookingService bookingService;

    // Constructor injection includes BookingService after moving booking logic out of RoomService
    public RoomController(RoomService roomService,
                          HotelService hotelService,
                          BookingService bookingService) {
        this.roomService = roomService;
        this.hotelService = hotelService;
        this.bookingService = bookingService;
    }

    // shows all rooms, or filter them by type, sea view, or max price in the room page filtering
    @GetMapping
    public String list(
            @RequestParam(name = "number", required = false) Integer roomNumber,
            @RequestParam(name = "type", required = false) String type,
            @RequestParam(name = "sea", required = false) Boolean sea,
            @RequestParam(name = "maxPrice", required = false) BigDecimal maxPrice,
            Model model) {

        List<Room> rooms = roomService.findRoomsForOverview(roomNumber, type, sea, maxPrice);

        // Add filtered results and other attributes to Model (to display on HTML)
        model.addAttribute("rooms", rooms);
        model.addAttribute("types", RoomType.values());
        model.addAttribute("selType", type);
        model.addAttribute("selSea", sea);
        model.addAttribute("selPrice", maxPrice);

        return "rooms";   // Return Thymeleaf page (rooms.html)
    }

    /// Add Room Form by Admin only
    // Shows the form when clicks “Add Room”
    @PreAuthorize("hasRole('ADMIN')")
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

    /// for handling POST request when submits the “Add Room” form
    @PreAuthorize("hasRole('ADMIN')")
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

        roomService.createRoom(
                roomForm.getNumber(), roomForm.getType(), roomForm.getPricePerNight(), roomForm.isSeaView(),
                roomForm.getPhotoUrl(), roomForm.getDescription(), roomForm.getHotelId());


        return "redirect:/rooms";   // Redirect back to list of rooms after successful submission
    }

    // show Room details for one specific room by its room id
    @GetMapping("/{roomId}")
    public String showRoomDetails(@PathVariable Long roomId,
                                  @RequestParam(required = false) Boolean created,
                                  Model model) {
        log.debug("Loading room details for room {}", roomId);

        var roomDetails = roomService.getRoomDetailsForCurrentUser(roomId);

        // Add a room and the bookings this user is allowed to see
        model.addAttribute("room", roomDetails.room());
        model.addAttribute("guests", roomDetails.stays()); // already sorted in service/repository
        model.addAttribute("isCustomer", roomDetails.customer());
        model.addAttribute("showRoomBookings", roomDetails.showRoomBookings());
        model.addAttribute("today", roomDetails.today());
        model.addAttribute("showCreatedToast", Boolean.TRUE.equals(created));

        return "room-detail";
    }

    // Show Booking Page for Admin and ApplicationUser
    @PreAuthorize("hasAnyRole('STAFF','ADMIN','CUSTOMER')")
    @GetMapping("/{roomId}/book")
    public String showBookingPage(@PathVariable Long roomId,
                                  Model model) {
        log.debug("Loading booking form for room {}", roomId);

        model.addAttribute("booking", bookingService.getBookingFormDetails(roomId));

        return "book-room";
    }

    // Processes a booking through BookingService so RoomService stays focused on rooms
    @PreAuthorize("hasAnyRole('STAFF','ADMIN','CUSTOMER')")
    @PostMapping("/{roomId}/book")
    public String processBooking(@PathVariable Long roomId,
                                 @RequestParam(required = false) Long guestId,
                                 // explicit date format
                                 @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
                                 @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {

        log.debug("Processing booking for room {} with guest {}", roomId, guestId);

        // Attempts booking; redirects on success; returns form on failure
        try {
            bookingService.bookRoomForCurrentUser(roomId, guestId, checkIn, checkOut);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "booking.success"
            );

            return "redirect:/rooms/" + roomId;

        } catch (BookingException ex) {
            model.addAttribute("booking", bookingService.getBookingFormDetails(roomId));

            // keep previously entered values
            model.addAttribute("selectedGuestId", guestId);
            model.addAttribute("selectedCheckIn", checkIn);
            model.addAttribute("selectedCheckOut", checkOut);

            model.addAttribute("errorCode", ex.getCode());

            return "book-room";
        }
    }

    /// delete a room by its id by Admin
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{roomId}/delete")
    public String deleteRoom(@PathVariable Long roomId) {
        log.debug("Deleting room {}", roomId);

        roomService.deleteRoom(roomId);
        return "redirect:/rooms?deleted";
    }

    /// Room description by Admin
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{roomId}/edit-description")
    public String editRoomDescriptionForm(@PathVariable Long roomId,
                                          Model model) {
        log.debug("Loading edit room description form for room {}", roomId);

        Room room = roomService.getRoomById(roomId);

        model.addAttribute("room", room);

        return "edit-room-description"; // Thymeleaf page
    }

    @PreAuthorize("hasRole('ADMIN')")
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

        return "error/404";
    }
}