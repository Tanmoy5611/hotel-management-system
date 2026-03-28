package be.kdg.prog5.hotels.business;

import be.kdg.prog5.hotels.domain.ActivityLog;
import be.kdg.prog5.hotels.domain.ActivityType;
import be.kdg.prog5.hotels.domain.ApplicationUser;

import java.util.List;

public interface ActivityLogService {

    // save a new activity
    void log(ActivityType action, String description, ApplicationUser user);

    // get latest 10 activities for admin dashboard
    List<ActivityLog> getRecentLogs();

}