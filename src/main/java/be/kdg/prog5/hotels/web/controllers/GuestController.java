package be.kdg.prog5.hotels.web.controllers;

import be.kdg.prog5.hotels.business.GuestService;
import be.kdg.prog5.hotels.business.RoomService;
import be.kdg.prog5.hotels.domain.Guest;
import be.kdg.prog5.hotels.domain.Room;
import be.kdg.prog5.hotels.domain.Stay;
import be.kdg.prog5.hotels.domain.VIPGuest;
import be.kdg.prog5.hotels.viewmodel.GuestForm;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
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

        // Load guest by ID using JPA repository
        // Single database hit fetches everything optimized
        Guest guest = guestService.getGuestWithDetails(guestId);
        // Build the room rows directly from the guest's stays (In-memory)
        List<Map<String, Object>> roomRows = new ArrayList<>();

        // Builds list of room details with discounts
        for (Stay stay : guest.getStays()) {

            Room room = stay.getRoom();

            Map<String, Object> row = new HashMap<>();

            BigDecimal discount = stay.getGuest().getDiscountPercentage();
            BigDecimal totalPrice = stay.getTotalPrice();   // Price before discount
            BigDecimal finalPrice = stay.getFinalPrice();   // Price after discount


            row.put("room", room);
            row.put("checkIn", stay.getCheckInDate());
            row.put("checkOut", stay.getCheckOutDate());
            row.put("nights", stay.getNumberOfNights());
            row.put("discount", discount);
            row.put("totalPrice", totalPrice);
            row.put("finalPrice", finalPrice);

            roomRows.add(row);
        }

        // Add to model
        model.addAttribute("guest", guest);
        model.addAttribute("roomRows", roomRows);

        return "guest-detail";
    }

    // Delete guest by Admin
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/guests/{guestId}/delete")
    public String deleteGuest(@PathVariable Long guestId) {
        log.debug("Deleting guest {}", guestId);

        guestService.deleteGuest(guestId);

        return "redirect:/guests";
    }

    // Spring Data Queries -  Vip search
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/guests/vip")
    public String showVipGuests(Model model) {
        log.debug("Loading all VIP guests from GuestService");

        model.addAttribute("guests", guestService.getVipGuests());

        return "guests";   // reuse guests.html
    }


    // Guest Name Search
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
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


    @PreAuthorize("hasAnyRole('USER','ADMIN')")
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

        // If room selected -> dates required
        if (guestForm.getRoomId() != null) {

            if (guestForm.getCheckIn() == null) {
                bindingResult.rejectValue("checkIn", "checkIn.required", "Check-in date is required");
            }

            if (guestForm.getCheckOut() == null) {
                bindingResult.rejectValue("checkOut", "checkOut.required", "Check-out date is required");
            }

            if (!bindingResult.hasErrors()
                    && guestForm.getCheckOut().isBefore(guestForm.getCheckIn())) {

                bindingResult.rejectValue("checkOut", "checkOut.invalid",
                        "Check-out must be after check-in");
            }
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("guestForm", guestForm);
            model.addAttribute("rooms", roomService.getAllRooms());

            return "add-guest";
        }

        // ViewModel to Domain Conversion
        Guest guest;

        // Creates guest instance; VIP if discount applies
        if (guestForm.getDiscountPercentage() != null &&
                guestForm.getDiscountPercentage().compareTo(BigDecimal.ZERO) > 0) {
            guest = new VIPGuest(
                    guestForm.getFullName(),
                    guestForm.getDob(),
                    guestForm.getEmail(),
                    guestForm.getAvatarUrl(),
                    guestForm.getDiscountPercentage()
            );
        } else {
            guest = new Guest(
                    guestForm.getFullName(),
                    guestForm.getDob(),
                    guestForm.getEmail(),
                    guestForm.getAvatarUrl()
            );
        }

        // Room Assignment
        guestService.createGuestWithRoom(
                guest,
                guestForm.getRoomId(),
                guestForm.getCheckIn(),
                guestForm.getCheckOut()
        );

        return "redirect:/guests";
    }
}