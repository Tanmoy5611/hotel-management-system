package be.kdg.prog5.hotels.web.controllers;

import be.kdg.prog5.hotels.business.BookingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/bookings")
@PreAuthorize("hasRole('ADMIN')")
public class AdminBookingController {

    private static final Logger log = LoggerFactory.getLogger(AdminBookingController.class);

    private final BookingService bookingService;

    // Injects BookingService so the controller stays focused on HTTP flow
    public AdminBookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    // Shows the admin page with current and future bookings
    @GetMapping
    public String showBookings(Model model) {
        log.debug("Loading admin bookings page");

        model.addAttribute("bookings", bookingService.getCurrentBookings());

        return "admin-bookings";
    }

    // Handles the cancel booking form and redirects back with a success message
    @PostMapping("/{stayId}/cancel")
    public String cancelBooking(@PathVariable Long stayId,
                                RedirectAttributes redirectAttributes) {
        log.debug("Cancelling booking {}", stayId);

        bookingService.cancelBooking(stayId);
        redirectAttributes.addFlashAttribute("successMessage", "Booking cancelled successfully.");

        return "redirect:/admin/bookings";
    }
}