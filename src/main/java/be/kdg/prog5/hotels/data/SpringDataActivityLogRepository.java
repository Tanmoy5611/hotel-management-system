package be.kdg.prog5.hotels.data;

import be.kdg.prog5.hotels.domain.ActivityLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataActivityLogRepository extends JpaRepository<ActivityLog, Long> {

    // Fetch the associated user together with each paginated log so rendering the table
    // does not trigger an additional query for every row
    @EntityGraph(attributePaths = "user")
    Page<ActivityLog> findAllByOrderByTimestampDescIdDesc(Pageable pageable);
}