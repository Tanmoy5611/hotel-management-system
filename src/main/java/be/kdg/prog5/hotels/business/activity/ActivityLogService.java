package be.kdg.prog5.hotels.business.activity;

import be.kdg.prog5.hotels.domain.ActivityLog;
import be.kdg.prog5.hotels.domain.ActivityType;
import be.kdg.prog5.hotels.domain.ApplicationUser;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ActivityLogService {

    // save a new activity
    void log(ActivityType action, String description, ApplicationUser user);

    Page<ActivityLog> getActivityLogs(int page);

    List<Integer> getVisiblePageNumbers(Page<ActivityLog> activityPage);
}