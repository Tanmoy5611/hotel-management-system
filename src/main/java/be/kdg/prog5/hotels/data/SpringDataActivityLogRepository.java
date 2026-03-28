package be.kdg.prog5.hotels.data;

import be.kdg.prog5.hotels.domain.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataActivityLogRepository extends JpaRepository<ActivityLog, Long> {

    // get the 10 most recent activity logs
    List<ActivityLog> findTop10ByOrderByTimestampDesc();
}