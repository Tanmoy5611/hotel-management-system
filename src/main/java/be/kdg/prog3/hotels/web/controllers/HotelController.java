package be.kdg.prog3.hotels.web.controllers;
import be.kdg.prog3.hotels.business.GuestService;
import be.kdg.prog3.hotels.business.HotelService;
import be.kdg.prog3.hotels.business.RoomService;
import be.kdg.prog3.hotels.domain.Guest;
import be.kdg.prog3.hotels.domain.Hotel;
import be.kdg.prog3.hotels.domain.Room;
import be.kdg.prog3.hotels.viewmodel.HotelForm;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// this controller handles all web requests for the hotels page
@Controller
@RequestMapping("/hotels")          // base url for all methods in this controller
public class HotelController {        // All URLs in this controller start with /hotels

    // Logger for debugging messages in console
    private static final Logger log = LoggerFactory.getLogger(HotelController.class);
    private final HotelService hotelService;   // injecting the HotelService to access business logic
    private final RoomService roomService;
    private final GuestService guestService;

    // Constructor injection (Spring will automatically provide the HotelService bean)
    public HotelController(HotelService hotelService,
                           RoomService roomService,
                           GuestService guestService) {
        this.hotelService = hotelService;
        this.roomService = roomService;
        this.guestService = guestService;
    }

    // method of showing all Hotels (list) + filter them based on: minStars +  opened date
    // The controller only orchestrates which business method to call based on user input.
    @GetMapping
    public String list(@RequestParam(name = "minStars", required = false) Integer minStars,
                       @RequestParam(name = "opened", required = false)
                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate opened,
                       @RequestParam(name = "name", required = false) String name,
                       @RequestParam(name = "sort", required = false) String sort,
                       Model model) {

        log.debug("Listing hotels with filters: minStars={}, opened={}, name='{}', sort={}",
                minStars, opened, name, sort);

        List<Hotel> hotels;

        // if user typed a hotel name then search by name (Spring Data or fallback)
        if (name != null && !name.isBlank()) {
            hotels = hotelService.searchByName(name.trim());
        }

        // filter by minimum stars (and optional opened date) (uses @Query) ---
        else if (minStars != null) {
            hotels = hotelService.getHotelsByMinStarsAndDate(minStars,
                    opened != null ? opened.toString() : null);
        }

        // no filters at all: show all hotels (Default)
        else {
            hotels = hotelService.getAllHotels();
        }

        // Optional sorting in memory (Java)
        if (sort != null && !sort.isBlank()) {
            hotels = new ArrayList<>(hotels);    // make list mutable for sorting

            switch (sort) {
                case "name" -> hotels.sort((a, b) -> a.getName().compareToIgnoreCase(b.getName()));
                case "stars" -> hotels.sort((a, b) -> Integer.compare(b.getStars(), a.getStars()));
            }
        }

        // Add data to model for Thymeleaf template
        model.addAttribute("hotels", hotels);
        model.addAttribute("total", hotels.size());

        // keep values in UI
        model.addAttribute("selectedMinStars", minStars);
        model.addAttribute("selectedOpened", opened);
        model.addAttribute("selectedName", name);
        model.addAttribute("selectedSort", sort);

        // Return the name of the Thymeleaf template (hotels.html)
        return "hotels";
    }

    // Add Hotel Form
    // This method shows the "Add Hotel" form when visits "/hotels/add"
    @GetMapping("/add")
    public String addForm(Model model) {
        // Create empty Hotel object to bind form fields using HotelForm class
        model.addAttribute("hotelForm", new HotelForm()); // lowercase name convention

        return "add-hotel";   // Return the add-hotel.html template
    }

    // Save new Hotel
    // This method processes the HotelForm submission for adding  new hotel
    @PostMapping("/add")
    public String addSubmit(@ModelAttribute("hotelForm") @Valid HotelForm hotelForm,
                            BindingResult bindingResult) {

        // Check form validation errors (from annotations in HotelForm)
        if (bindingResult.hasErrors()) {
            log.debug("Validation errors found while adding hotel: {}", bindingResult.getAllErrors());

            // Return same page, errors will be displayed under fields
            return "add-hotel";
        }

        // JPA - Using full constructor because no-args is protected
        Hotel hotel = new Hotel(
                null,   // ID auto-generated in service layer
                hotelForm.getName(),
                hotelForm.getCity(),
                hotelForm.getCountry(),
                hotelForm.getOpenedOn(),
                hotelForm.getStars(),
                hotelForm.isHasSpa(),
                hotelForm.getImageUrl(),
                hotelForm.getDescription()
        );

        // Log and save the new hotel through the service layer
        log.debug("Creating new hotel: {}", hotel);
        hotelService.createHotel(hotel);  // Save via business layer

        // Redirect to /hotels after successfully adding a new hotel
        return "redirect:/hotels";
    }


    //  Show 1 hotel + its rooms + guests per room
    @GetMapping("/{id}")
    public String showHotelDetails(@PathVariable String id, Model model) {

        //  Load hotel from DB using business service
        Hotel hotel = hotelService.getHotelById(id);
        if (hotel == null) {

            // If hotel not found, go back to list
            return "redirect:/hotels";
        }

        // Load rooms for this hotel using RoomService (JDBC compatible) (Many-to-One)
        List<Room> rooms = roomService.getRoomsByHotel(id);

        // Map guests PER ROOM using ROOM ID
        // For each room, load guests (Many-to-Many room ↔ guest)
        Map<Long, List<Guest>> guestsPerRoom = new HashMap<>();
        for (Room room : rooms) {
            guestsPerRoom.put(
                    room.getId(),
                    guestService.getGuestsByRoom(room.getId()));
        }

        // Calculate total number of guests staying in this hotel
        int totalGuests = guestsPerRoom.values().stream()
                .mapToInt(List::size)
                .sum();

        // Add the found hotel to the model so Thymeleaf can display it
        model.addAttribute("hotel", hotel);
        model.addAttribute("rooms", rooms);
        model.addAttribute("guestsPerRoom", guestsPerRoom);
        model.addAttribute("totalGuests", totalGuests);

        return "hotel-detail";            // hotel-detail.html
    }

    @PostMapping("/{id}/delete")
    public String deleteHotel(@PathVariable String id) {
        log.debug("Deleting hotel {}", id);
        hotelService.deleteHotel(id);          // Business layer handles cascading / repo

        return "redirect:/hotels";
    }


    /// Hotel description
    @GetMapping("/{id}/edit-description")
    public String editHotelDescriptionForm(@PathVariable String id, Model model) {

        Hotel hotel = hotelService.getHotelById(id);

        if (hotel == null) {
            return "redirect:/hotels";
        }

        model.addAttribute("hotel", hotel);

        return "edit-hotel-description";
    }

    @PostMapping("/{id}/edit-description")
    public String updateHotelDescription(
            @PathVariable String id,
            @RequestParam String description) {

        hotelService.updateHotelDescription(id, description);
        return "redirect:/hotels/" + id;
    }

}