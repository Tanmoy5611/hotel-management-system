package be.kdg.prog5.hotels.web.controllers;

import be.kdg.prog5.hotels.business.ActivityLogService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/activity")
@PreAuthorize("hasRole('ADMIN')")
public class AdminActivityController {

    private final ActivityLogService activityLogService;

    // Injects ActivityLogService to load recent system activity
    public AdminActivityController(ActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
    }

    // Shows the standalone admin activity management page
    @GetMapping
    public String showActivity(Model model) {
        model.addAttribute("logs", activityLogService.getRecentLogs());
        return "admin-activity";
    }
}