package be.kdg.prog5.hotels.business.activity;

import be.kdg.prog5.hotels.data.SpringDataActivityLogRepository;
import be.kdg.prog5.hotels.domain.ActivityLog;
import be.kdg.prog5.hotels.domain.ActivityType;
import be.kdg.prog5.hotels.domain.ApplicationUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

@Service
@Transactional
public class ActivityLogServiceImpl implements ActivityLogService {

    private static final int ACTIVITY_PAGE_SIZE = 10;

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

    @Override
    @Transactional(readOnly = true)
    public Page<ActivityLog> getActivityLogs(int page) {
        int requestedPage = Math.max(page, 0);
        Page<ActivityLog> activityPage = repo.findAllByOrderByTimestampDescIdDesc(
                PageRequest.of(requestedPage, ACTIVITY_PAGE_SIZE)
        );

        if (activityPage.getTotalPages() > 0 && requestedPage >= activityPage.getTotalPages()) {
            return repo.findAllByOrderByTimestampDescIdDesc(
                    PageRequest.of(activityPage.getTotalPages() - 1, ACTIVITY_PAGE_SIZE)
            );
        }

        return activityPage;
    }

    @Override
    public List<Integer> getVisiblePageNumbers(Page<ActivityLog> activityPage) {
        int lastPage = activityPage.getTotalPages() - 1;
        int startPage = Math.max(0, activityPage.getNumber() - 4);
        int endPage = Math.min(lastPage, startPage + 4);
        startPage = Math.max(0, endPage - 4);

        return IntStream.rangeClosed(startPage, endPage)
                .boxed()
                .toList();
    }
}