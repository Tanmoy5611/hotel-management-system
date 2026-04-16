package be.kdg.prog5.hotels.data;

import be.kdg.prog5.hotels.domain.Guest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// Spring Data JPA repository that automatically provides CRUD and query methods
@Repository       // Marks this interface as persistence layer component managed by Spring
public interface SpringDataGuestRepository extends JpaRepository<Guest, Long> {

    // Query Method VIP guests
    @Query("""
    SELECT g FROM VIPGuest g
""")
    List<Guest> findVipGuests();

    @Query("""
            SELECT g
            FROM Guest g
            WHERE LOWER(g.fullName) LIKE LOWER(CONCAT('%', :query, '%'))
              AND (:minRooms IS NULL OR SIZE(g.stays) >= :minRooms)
            """)
    List<Guest> searchGuests(@Param("query") String query,
                             @Param("minRooms") Integer minRooms);

    //  Optimized query to load the Guest aggregate with Stays, Rooms, and Hotels
    @Query("""
    SELECT DISTINCT g FROM Guest g
    LEFT JOIN FETCH g.stays s
    LEFT JOIN FETCH s.room r
    LEFT JOIN FETCH r.hotel
    WHERE g.id = :id
""")
    Optional<Guest> findByIdWithDetails(@Param("id") Long id);
}