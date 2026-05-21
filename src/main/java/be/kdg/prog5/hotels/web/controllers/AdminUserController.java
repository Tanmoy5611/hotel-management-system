package be.kdg.prog5.hotels.web.controllers;

import be.kdg.prog5.hotels.business.ApplicationUserService;
import be.kdg.prog5.hotels.config.AppConstants;
import be.kdg.prog5.hotels.viewmodel.RegisterForm;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')") // only admins can access this controller
public class AdminUserController {

    private final ApplicationUserService applicationUserService;

    // Injects user service for admin dashboard and user management actions
    public AdminUserController(ApplicationUserService applicationUserService) {
        this.applicationUserService = applicationUserService;
    }

    // Shows the clean admin dashboard with navigation cards only
    @GetMapping
    public String showDashboard() {
        return "admin-users";
    }

    // Shows the standalone user management table
    @GetMapping("/manage")
    public String showUsers(Model model) {
        model.addAttribute("users", applicationUserService.getAllUsers());

        // protected admin email is used by the view to hide delete/role actions
        model.addAttribute("protectedAdminEmail", AppConstants.PROTECTED_ADMIN_EMAIL);

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

        // Try to create the user
        var error = applicationUserService.createUser(registerForm);

        // If business validation error occurs, return to the form
        if (error.isPresent()) {
            model.addAttribute("errorMessage", error.get());
            model.addAttribute("registerForm", registerForm);
            return "add-user";
        }

        // Redirect to user list after successful creation
        return "redirect:/admin/users/manage";
    }

    // Deletes a user by id from the management page
    @PostMapping("/{id}/delete")
    public String deleteUser(@PathVariable Long id) {
        applicationUserService.deleteUser(id);
        return "redirect:/admin/users/manage";
    }

    // Toggles a user role between USER and ADMIN
    @PostMapping("/{id}/toggle-role")
    public String toggleRole(@PathVariable Long id) {
        applicationUserService.toggleUserRole(id);
        return "redirect:/admin/users/manage";
    }
}