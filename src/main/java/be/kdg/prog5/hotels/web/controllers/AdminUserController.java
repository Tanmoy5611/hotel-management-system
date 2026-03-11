package be.kdg.prog5.hotels.web.controllers;

import be.kdg.prog5.hotels.business.UserService;
import be.kdg.prog5.hotels.viewmodel.RegisterForm;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')") // only admins can access this controller
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    // show all users
    @GetMapping
    public String showUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin-users";
    }

    // show add user form
    @GetMapping("/add")
    public String showAddUserForm(Model model) {
        model.addAttribute("registerForm", new RegisterForm());
        return "add-user";
    }

    // create user
    @PostMapping("/add")
    public String addUser(@Valid @ModelAttribute RegisterForm registerForm,
                          Model model) {

        // Try to create the user
        var error = userService.createUser(registerForm);

        // If validation error occurs, return to the form
        if (error.isPresent()) {
            model.addAttribute("errorMessage", error.get());
            model.addAttribute("registerForm", registerForm);
            return "add-user";
        }

        // Redirect to user list after successful creation
        return "redirect:/admin/users";
    }

    // delete user
    @PostMapping("/{id}/delete")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/admin/users";
    }

    // toggle role USER <-> ADMIN
    @PostMapping("/{id}/toggle-role")
    public String toggleRole(@PathVariable Long id) {
        userService.toggleUserRole(id);
        return "redirect:/admin/users";
    }
}