package be.kdg.prog5.hotels.web.controllers;

import be.kdg.prog5.hotels.business.user.ApplicationUserService;
import be.kdg.prog5.hotels.business.customer.CustomerService;
import be.kdg.prog5.hotels.business.exceptions.ApplicationUserAlreadyExistsException;
import be.kdg.prog5.hotels.business.exceptions.ApplicationUserHasGuestsException;
import be.kdg.prog5.hotels.viewmodel.RegisterForm;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')") // only admins can access this controller
public class AdminUserController {

    private final ApplicationUserService applicationUserService;
    private final CustomerService customerService;

    // Injects user service for admin dashboard and user management actions
    public AdminUserController(ApplicationUserService applicationUserService,
                               CustomerService customerService) {
        this.applicationUserService = applicationUserService;
        this.customerService = customerService;
    }

    // Shows the clean admin dashboard with navigation cards only
    @GetMapping
    public String showDashboard() {
        return "admin-users";
    }

    // Shows the standalone user management table
    @GetMapping("/manage")
    public String showUsers(Model model) {
        model.addAttribute("accounts", applicationUserService.getAccountsForAdminPage());
        return "admin-users-manage";
    }

    // Shows the form for creating a new user
    @GetMapping("/add")
    public String showAddUserForm(Model model) {
        model.addAttribute("registerForm", new RegisterForm());
        return "add-user";
    }

    // Creates a user and returns to the management page
    @PostMapping("/add")
    public String addUser(@Valid @ModelAttribute RegisterForm registerForm,
                          BindingResult bindingResult,
                          Model model) {

        // If Jakarta validation errors occur, return to the form
        if (bindingResult.hasErrors()) {
            model.addAttribute("registerForm", registerForm);
            return "add-user";
        }

        try {
            applicationUserService.createUser(registerForm);
            return "redirect:/admin/users/manage";
        } catch (ApplicationUserAlreadyExistsException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("registerForm", registerForm);
            return "add-user";
        }
    }

    // Deletes a user by id from the management page
    @PostMapping("/{id}/delete")
    public String deleteUser(@PathVariable Long id,
                             RedirectAttributes redirectAttributes) {
        // Try to delete the user, catching the exception if the user has guests
        try {
            applicationUserService.deleteUser(id);
        } catch (ApplicationUserHasGuestsException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/admin/users/manage";
    }

    // Toggles a user role between STAFF and ADMIN
    @PostMapping("/{id}/toggle-role")
    public String toggleRole(@PathVariable Long id) {
        applicationUserService.toggleUserRole(id);
        return "redirect:/admin/users/manage";
    }

    @PostMapping("/customers/{id}/toggle-active")
    public String toggleCustomerActive(@PathVariable Long id) {
        // Customers do not change roles, admin only toggles active status
        customerService.toggleCustomerActive(id);
        return "redirect:/admin/users/manage";
    }
}