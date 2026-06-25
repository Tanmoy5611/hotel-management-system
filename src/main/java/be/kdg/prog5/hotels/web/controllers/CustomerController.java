package be.kdg.prog5.hotels.web.controllers;

import be.kdg.prog5.hotels.business.customer.CustomerService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/register")
    public String registrationForm() {
        // Public customer registration page
        return "customer-register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String fullName,
                           @RequestParam String email,
                           @RequestParam LocalDate dob,
                           @RequestParam String password,
                           RedirectAttributes redirectAttributes) {
        try {
            // Registration rules stay in CustomerService
            customerService.register(fullName, email, dob, password);
            redirectAttributes.addFlashAttribute("successMessage", "Your account is ready. Please sign in.");
            return "redirect:/login";
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
            return "redirect:/register";
        }
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/my")
    public String dashboard(Model model) {
        // Service prepares profile and bookings for the logged in customer
        var dashboard = customerService.getDashboardForCurrentCustomer();
        model.addAttribute("profile", dashboard.profile());
        model.addAttribute("bookings", dashboard.bookings());
        return "customer-dashboard";
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/my/bookings/{stayId}/cancel")
    public String cancel(@org.springframework.web.bind.annotation.PathVariable Long stayId,
                         RedirectAttributes redirectAttributes) {
        // Service checks that the booking belongs to this customer
        customerService.cancelOwnBookingForCurrentCustomer(stayId);
        redirectAttributes.addFlashAttribute("successMessage", "Booking cancelled successfully.");
        return "redirect:/my";
    }
}