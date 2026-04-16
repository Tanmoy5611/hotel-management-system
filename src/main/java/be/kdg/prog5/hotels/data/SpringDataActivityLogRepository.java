package be.kdg.prog5.hotels.data;

import be.kdg.prog5.hotels.domain.ActivityLog;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataActivityLogRepository extends JpaRepository<ActivityLog, Long> {

    // get the 10 most recent activity logs
    // @EntityGraph (it eagerly fetches the associated 'user') - allows to specify which lazy-loaded associations should be fetched eagerly for a specific query,
    // avoiding LazyInitializationException when the session closes and improving performance
    @EntityGraph(attributePaths = "user")
    List<ActivityLog> findTop10ByOrderByTimestampDesc();
}