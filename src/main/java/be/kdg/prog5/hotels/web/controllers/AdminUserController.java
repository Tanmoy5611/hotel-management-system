package be.kdg.prog5.hotels.web.controllers;

import be.kdg.prog5.hotels.business.ActivityLogService;
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
    private final ActivityLogService activityLogService;

    public AdminUserController(ApplicationUserService applicationUserService,
                               ActivityLogService activityLogService) {
        this.applicationUserService = applicationUserService;
        this.activityLogService = activityLogService;
    }

    // show all users
    @GetMapping
    public String showUsers(Model model) {
        model.addAttribute("users", applicationUserService.getAllUsers());

        // get recent activity logs (global logs) for admin page
        model.addAttribute("logs", activityLogService.getRecentLogs());

        // protected admin email is used by the view to hide delete/role actions
        model.addAttribute("protectedAdminEmail", AppConstants.PROTECTED_ADMIN_EMAIL);

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
        return "redirect:/admin/users";
    }

    // delete user
    @PostMapping("/{id}/delete")
    public String deleteUser(@PathVariable Long id) {
        applicationUserService.deleteUser(id);
        return "redirect:/admin/users";
    }

    // toggle role USER <-> ADMIN
    @PostMapping("/{id}/toggle-role")
    public String toggleRole(@PathVariable Long id) {
        applicationUserService.toggleUserRole(id);
        return "redirect:/admin/users";
    }
}