package be.kdg.prog5.hotels.business;

import be.kdg.prog5.hotels.domain.ActivityType;
import be.kdg.prog5.hotels.domain.ApplicationUser;
import be.kdg.prog5.hotels.web.security.SecurityService;
import org.springframework.stereotype.Service;

// Responsible for writing activity logs only when a logged-in user is available
// This keeps the null-user safety check in one place instead of repeating it in every service
@Service
public class SafeActivityLogger {

    private final SecurityService securityService;
    private final ActivityLogService activityLogService;

    public SafeActivityLogger(SecurityService securityService,
                              ActivityLogService activityLogService) {
        this.securityService = securityService;
        this.activityLogService = activityLogService;
    }

    // avoids repeating null-user checks for activity logging
    public void log(ActivityType type, String description) {
        ApplicationUser user = securityService.getLoggedInUserSafe();

        if (user != null) {
            activityLogService.log(type, description, user);
        }
    }
}