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

    // Query Method VIP guests using inheritance
    @Query("""
            SELECT g
            FROM Guest g
            WHERE TYPE(g) = VIPGuest
            """)
    List<Guest> findVipGuests();

    // Query Method: Search by name (SELECT g FROM Guest g WHERE lower(g.fullName) LIKE lower('%text%') )
    List<Guest> findByFullNameContainingIgnoreCase(String text);

    // Custom @Query: (JPQL query uses the size of the rooms collection to find guests who booked multiple rooms)
    @Query("""
            SELECT g
            FROM Guest g
            WHERE size(g.stays) >= :minRooms
            """)
    List<Guest> findGuestsWithMoreThanRooms(@Param("minRooms") int minRooms);

    //  DISTINCT prevents duplicates if multiple stays
    @Query("""
            SELECT DISTINCT g
            FROM Guest g
            JOIN g.stays s
            WHERE s.room.id = :roomId
            """)
    List<Guest> findByRoom(@Param("roomId") Long roomId);

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
