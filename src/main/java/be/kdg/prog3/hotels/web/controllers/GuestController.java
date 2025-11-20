package be.kdg.prog3.hotels.web.controllers;

import be.kdg.prog3.hotels.business.GuestService;
import be.kdg.prog3.hotels.domain.Guest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

// Controller responsible for displaying all guests

@Controller
public class GuestController {
    private static final Logger log = LoggerFactory.getLogger(GuestController.class);
    private final GuestService guestService;

    // Constructor injection
    public GuestController(GuestService guestService) {
        this.guestService = guestService;
    }

    // Display all guests (list view)
    @GetMapping("/guests")
    public String showGuests(Model model) {
        log.debug("Loading all guests from GuestService");
        List<Guest> guests = guestService.getAllGuests();
        model.addAttribute("guests", guests);
        return "guests";
    }
}