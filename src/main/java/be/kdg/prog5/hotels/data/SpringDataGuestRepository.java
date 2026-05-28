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
            SELECT g
            FROM VIPGuest g
            JOIN FETCH g.owner
            """)
    List<Guest> findVipGuests();

    // Guest overview uses the owner email for permissions and display
    @Query("""
            SELECT g
            FROM Guest g
            JOIN FETCH g.owner
            """)
    List<Guest> findAllWithOwner();

    // Search Guests on the Clients page
    @Query("""
            SELECT g
            FROM Guest g
            JOIN FETCH g.owner
            WHERE (LOWER(g.fullName) LIKE LOWER(CONCAT('%', :query, '%'))
                   OR LOWER(g.email) LIKE LOWER(CONCAT('%', :query, '%')))
              AND (:minRooms IS NULL OR SIZE(g.stays) >= :minRooms)
            """)
    List<Guest> searchGuests(@Param("query") String query,
                             @Param("minRooms") Integer minRooms);

    // Checks duplicate guest emails during CSV import
    boolean existsByEmailIgnoreCase(String email);

    // Used before deleting an application user, because Guest.owner is mandatory
    boolean existsByOwner_Id(Long ownerId);

    //  Optimized query to load the Guest aggregate with Stays, Rooms, and Hotels
    @Query("""
            SELECT DISTINCT g
            FROM Guest g
            JOIN FETCH g.owner
            LEFT JOIN FETCH g.stays s
            LEFT JOIN FETCH s.room r
            LEFT JOIN FETCH r.hotel
            WHERE g.id = :id
            """)
    Optional<Guest> findByIdWithDetails(@Param("id") Long id);
}