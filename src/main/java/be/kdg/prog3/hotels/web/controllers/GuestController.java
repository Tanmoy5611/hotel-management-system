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

// Controller responsible for displaying all guests

@Controller
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
    public String showGuestDetails(@PathVariable long id, Model model) {

        /* Load guest by ID using JDBC repository
        Guest guest = guestService.getGuestById(id); */

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
    public String deleteGuest(@PathVariable long id) {
        guestService.deleteGuest(id);
        return "redirect:/guests";
    }

    // Spring Data Queries

    @GetMapping("/guests/vip")
    public String showVipGuests(Model model) {
        model.addAttribute("guests", guestService.getVipGuests());
        return "guests";   // reuse guests.html
    }

    @GetMapping("/guests/search")
    public String searchGuests(@RequestParam("q") String query, Model model) {
        model.addAttribute("guests", guestService.searchGuestsByName(query));
        model.addAttribute("searchQuery", query);
        return "guests";   // reuse guests.html
    }

    @GetMapping("/guests/manyRooms")
    public String showGuestsWithManyRooms(@RequestParam("min") int minRooms, Model model) {
        model.addAttribute("guests", guestService.getGuestsWithManyRooms(minRooms));
        model.addAttribute("minRooms", minRooms);
        return "guests";   // reuse guests.html
    }

    // show add guest form
    @GetMapping("/guests/add")
    public String showAddGuestForm(Model model) {
        model.addAttribute("guestForm", new GuestForm());
        return "add-guest";
    }


    @PostMapping("/guests/add")
    public String processAddGuest(
            @Valid @ModelAttribute GuestForm guestForm,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("guestForm", guestForm);  // <-- REQUIRED FIX
            return "add-guest";
        }

        Guest guest;

        if (guestForm.isVip()) {
            guest = new VIPGuest(
                    guestForm.getFullName(),
                    guestForm.getDob(),
                    guestForm.getEmail(),
                    true,
                    guestForm.getAvatarUrl(),
                    guestForm.getDiscountPercentage()
            );
        } else {
            guest = new Guest(
                    guestForm.getFullName(),
                    guestForm.getDob(),
                    guestForm.getEmail(),
                    false,
                    guestForm.getAvatarUrl()
            );
        }

        guestService.createGuestWithRoom(guest, guestForm.getRoomNumber());
        return "redirect:/guests";
    }
}