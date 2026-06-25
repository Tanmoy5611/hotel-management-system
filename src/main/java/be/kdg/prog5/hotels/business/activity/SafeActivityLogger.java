package be.kdg.prog5.hotels.business.activity;

import be.kdg.prog5.hotels.data.SpringDataApplicationUserRepository;
import be.kdg.prog5.hotels.domain.ActivityType;
import be.kdg.prog5.hotels.domain.ApplicationUser;
import be.kdg.prog5.hotels.business.security.SecurityService;
import org.springframework.stereotype.Service;

// Responsible for writing activity logs only when a logged-in user is available
// This keeps the null-user safety check in one place instead of repeating it in every service
@Service
public class SafeActivityLogger {

    private final SecurityService securityService;
    private final ActivityLogService activityLogService;
    private final SpringDataApplicationUserRepository userRepository;

    public SafeActivityLogger(SecurityService securityService,
                              ActivityLogService activityLogService,
                              SpringDataApplicationUserRepository userRepository) {
        this.securityService = securityService;
        this.activityLogService = activityLogService;
        this.userRepository = userRepository;
    }

    // avoids repeating null-user checks for activity logging
    public void log(ActivityType type, String description) {
        ApplicationUser user = findLoggedInUser();

        if (user != null) {
            activityLogService.log(type, description, user);
        }
    }

    // Used for public actions where there is no logged-in user,
    // but the service already selected a safe owner/system user for the log
    public void logAs(ActivityType type, String description, ApplicationUser user) {
        if (user != null) {
            activityLogService.log(type, description, user);
        }
    }

    private ApplicationUser findLoggedInUser() {
        String email = securityService.getLoggedInUsername();
        if (email == null) {
            return null;
        }

        return userRepository.findByEmail(email).orElse(null);
    }
}