package be.kdg.prog3.hotels.web.controllers;

import be.kdg.prog3.hotels.business.GuestService;
import be.kdg.prog3.hotels.business.RoomService;
import be.kdg.prog3.hotels.domain.Guest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

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

    // get guest details + list of rooms they stayed in
    @GetMapping("/guests/{id}")
    public String showGuestDetails(@PathVariable long id, Model model) {

        // Load guest by ID using JDBC repository
        Guest guest = guestService.getGuestById(id);
        if (guest == null)
            return "redirect:/guests";

        // Load rooms of this guest via RoomRepository (many-to-many)
        var rooms = roomService.getRoomsByGuest(id);

        model.addAttribute("guest", guest);
        model.addAttribute("rooms", rooms);

        return "guest-detail";
    }

    @PostMapping("/guests/{id}/delete")
    public String deleteGuest(@PathVariable long id) {
        guestService.deleteGuest(id);
        return "redirect:/guests";
    }


}