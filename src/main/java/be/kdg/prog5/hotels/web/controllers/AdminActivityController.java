package be.kdg.prog5.hotels.web.controllers;

import be.kdg.prog5.hotels.business.activity.ActivityLogService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    public String showActivity(@RequestParam(defaultValue = "0") int page, Model model) {
        var activityPage = activityLogService.getActivityLogs(page);
        model.addAttribute("activityPage", activityPage);
        model.addAttribute("logs", activityPage.getContent());
        model.addAttribute("pageNumbers", activityLogService.getVisiblePageNumbers(activityPage));
        return "admin-activity";
    }
}