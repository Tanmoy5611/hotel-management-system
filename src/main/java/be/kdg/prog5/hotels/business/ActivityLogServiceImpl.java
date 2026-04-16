package be.kdg.prog5.hotels.business;

import be.kdg.prog5.hotels.data.SpringDataActivityLogRepository;
import be.kdg.prog5.hotels.domain.ActivityLog;
import be.kdg.prog5.hotels.domain.ActivityType;
import be.kdg.prog5.hotels.domain.ApplicationUser;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ActivityLogServiceImpl implements ActivityLogService {

    private final SpringDataActivityLogRepository repo;

    public ActivityLogServiceImpl(SpringDataActivityLogRepository repo) {
        this.repo = repo;
    }

    @Override
    public void log(ActivityType action, String description, ApplicationUser user) {

        // Create log using constructor
        LocalDateTime timestamp = LocalDateTime.now();

        ActivityLog log = new ActivityLog(
                action,
                description,
                timestamp,
                user
        );

        repo.save(log);
    }

    // method to get recent 10 logs
    @Override
    @Transactional(readOnly = true)
    public List<ActivityLog> getRecentLogs() {
        return repo.findTop10ByOrderByTimestampDesc();
    }
}