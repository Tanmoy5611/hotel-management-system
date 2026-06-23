package be.kdg.prog5.hotels.web.controllers;

import be.kdg.prog5.hotels.business.GuestService;
import be.kdg.prog5.hotels.business.exceptions.BookingException;
import be.kdg.prog5.hotels.business.exceptions.GuestAlreadyExistsException;
import be.kdg.prog5.hotels.business.RoomService;
import be.kdg.prog5.hotels.domain.Guest;
import be.kdg.prog5.hotels.viewmodel.GuestForm;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    /// Display all guests (list view)
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/guests")
    public String showGuests(Model model) {
        log.debug("Loading all guests from GuestService");

        List<Guest> guests = guestService.getAllGuests();
        model.addAttribute("guests", guests);

        return "guests";
    }

    /// get guest details + list of rooms they stayed in (many-to-many)
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/guests/{guestId}")
    public String showGuestDetails(@PathVariable Long guestId, Model model) {
        log.debug("Loading guest {}", guestId);

        var guestDetails = guestService.getGuestDetails(guestId);

        // Add to model
        model.addAttribute("guest", guestDetails.guest());
        model.addAttribute("roomRows", guestDetails.stays());

        return "guest-detail";
    }

    /// Delete guest by Admin or Owner (The user)
    @PreAuthorize("@guestAuthorizationService.canDeleteGuest(#guestId, authentication)")
    @PostMapping("/guests/{guestId}/delete")
    public String deleteGuest(@PathVariable Long guestId) {
        log.debug("Deleting guest {}", guestId);

        guestService.deleteGuest(guestId);

        return "redirect:/guests";
    }

    // Spring Data Queries - Vip search
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/guests/vip")
    public String showVipGuests(Model model) {
        log.debug("Loading all VIP guests from GuestService");

        model.addAttribute("guests", guestService.getVipGuests());

        return "guests";   // reuse guests.html
    }

    // Guest name search and minimum rooms filter
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/guests/search")
    public String searchGuests(
            @RequestParam(name = "q", required = false) String query,
            @RequestParam(name = "min", required = false) Integer minRooms,
            Model model
    ) {
        log.debug("Searching guests with query {} and minRooms {}", query, minRooms);

        // Only HTTP-level short-circuit: nothing to search -> go back to list
        if ((query == null || query.isBlank()) && minRooms == null) {
            return "redirect:/guests";
        }

        List<Guest> guests = guestService.searchGuests(query, minRooms);

        model.addAttribute("guests", guests);
        model.addAttribute("searchQuery", query);
        model.addAttribute("minRooms", minRooms);

        return "guests";
    }

    // show add guest form
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/guests/add")
    public String showAddGuestForm(Model model) {
        model.addAttribute("guestForm", new GuestForm());
        model.addAttribute("rooms", roomService.getAllRooms());

        return "add-guest";
    }

    // process add guest form
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping("/guests/add")
    public String processAddGuest(
            @Valid @ModelAttribute GuestForm guestForm,    //  Validation before business logic
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("guestForm", guestForm);
            model.addAttribute("rooms", roomService.getAllRooms());

            return "add-guest";
        }

        try {
            // Controller passes raw form data - service decides Guest vs VIPGuest
            guestService.createGuestWithRoom(
                    guestForm.getFullName(),
                    guestForm.getDob(),
                    guestForm.getEmail(),
                    guestForm.getAvatarUrl(),
                    guestForm.getDiscountPercentage(),
                    guestForm.getRoomId(),
                    guestForm.getCheckIn(),
                    guestForm.getCheckOut()
            );
        } catch (GuestAlreadyExistsException ex) {
            bindingResult.rejectValue("email", "email.duplicate", ex.getMessage());
            model.addAttribute("guestForm", guestForm);
            model.addAttribute("rooms", roomService.getAllRooms());
            return "add-guest";
        } catch (BookingException ex) {
            bindingResult.reject("booking.invalid", ex.getCode());
            model.addAttribute("guestForm", guestForm);
            model.addAttribute("rooms", roomService.getAllRooms());
            return "add-guest";
        }

        return "redirect:/guests";
    }
}