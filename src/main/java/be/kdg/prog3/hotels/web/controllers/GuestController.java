package be.kdg.prog3.hotels.web.controllers;
import be.kdg.prog3.hotels.business.GuestService;
import be.kdg.prog3.hotels.business.RoomService;
import be.kdg.prog3.hotels.domain.Guest;
import be.kdg.prog3.hotels.domain.Room;
import be.kdg.prog3.hotels.domain.VIPGuest;
import be.kdg.prog3.hotels.viewmodel.GuestForm;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Controllers handle HTTP requests and delegate all business logic to services
@Controller        // Marks this class as a Spring MVC controller
public class GuestController {
    private static final Logger log = LoggerFactory.getLogger(GuestController.class);
    private final GuestService guestService;
    private final RoomService roomService;

    // Constructor injection
    public GuestController(GuestService guestService, RoomService roomService) {
        this.guestService = guestService;
        this.roomService = roomService;
    }

    // Display all guests (list view)
    @GetMapping("/guests")
    public String showGuests(Model model) {
        log.debug("Loading all guests from GuestService");
        List<Guest> guests = guestService.getAllGuests();
        model.addAttribute("guests", guests);

        return "guests";
    }

    // get guest details + list of rooms they stayed in (many-to-many)
    @GetMapping("/guests/{id}")
    public String showGuestDetails(@PathVariable Long id, Model model) {

        // Load guest by ID using JPA repository
        Guest guest = guestService.getGuestById(id);

        if (guest == null)
            return "redirect:/guests";

        // Load rooms of this guest via RoomRepository (many-to-many)
        var rooms = roomService.getRoomsByGuest(id);

        // Build a table list (DTO-like)
        List<Map<String, Object>> roomRows = new ArrayList<>();

        for (Room r : rooms) {
            Map<String, Object> row = new HashMap<>();

            double discount = guest.getDiscountPercentage();
            double finalPrice = roomService.calculateDiscountedPrice(r, guest);

            row.put("room", r);
            row.put("discount", discount);
            row.put("finalPrice", finalPrice);

            roomRows.add(row);
        }

        // Add to model
        model.addAttribute("guest", guest);
        model.addAttribute("roomRows", roomRows);

        return "guest-detail";
    }

    @PostMapping("/guests/{id}/delete")
    public String deleteGuest(@PathVariable Long id) {
        guestService.deleteGuest(id);

        return "redirect:/guests";
    }

    // Spring Data Queries
    // Vip search
    @GetMapping("/guests/vip")
    public String showVipGuests(Model model) {
        model.addAttribute("guests", guestService.getVipGuests());

        return "guests";   // reuse guests.html
    }


    // Guest Name Search
    @GetMapping("/guests/search")
    public String searchGuests(
            @RequestParam(name = "q", required = false) String query,
            Model model
    ) {
        if (query == null || query.isBlank()) {
            return "redirect:/guests";
        }

        model.addAttribute("guests", guestService.searchGuestsByName(query));
        model.addAttribute("searchQuery", query);

        return "guests";
    }

    @GetMapping("/guests/manyRooms")
    public String showGuestsWithManyRooms(
            @RequestParam(name = "min", required = false) Integer minRooms,
            Model model
    ) {
        // If empty or invalid -> just go back to normal guests list
        if (minRooms == null || minRooms < 1) {
            return "redirect:/guests";
        }

        model.addAttribute("guests", guestService.getGuestsWithManyRooms(minRooms));
        model.addAttribute("minRooms", minRooms);

        // keep search bar stable (optional but nice)
        model.addAttribute("searchQuery", "");

        return "guests";
    }

    // show add guest form
    @GetMapping("/guests/add")
    public String showAddGuestForm(Model model) {
        model.addAttribute("guestForm", new GuestForm());
        model.addAttribute("rooms", roomService.getAllRooms());

        return "add-guest";
    }

    @PostMapping("/guests/add")
    public String processAddGuest(
            @Valid @ModelAttribute GuestForm guestForm,    //  Validation before business logic
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("guestForm", guestForm);

            return "add-guest";
        }

        // ViewModel to Domain Conversion
        Guest guest;

        if (guestForm.isVip()) {
            // VIP guest
            guest = new VIPGuest(
                    guestForm.getFullName(),
                    guestForm.getDob(),
                    guestForm.getEmail(),
                    true,
                    guestForm.getAvatarUrl(),
                    guestForm.getDiscountPercentage()
            );
        } else {
            // Normal guest - polymorphism
            guest = new Guest(
                    guestForm.getFullName(),
                    guestForm.getDob(),
                    guestForm.getEmail(),
                    false,
                    guestForm.getAvatarUrl()
            );
        }

        // Room Assignment
        guestService.createGuestWithRoom(guest, guestForm.getRoomId());

        return "redirect:/guests";
    }
}