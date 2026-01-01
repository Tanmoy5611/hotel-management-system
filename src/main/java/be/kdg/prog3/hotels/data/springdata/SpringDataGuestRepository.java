package be.kdg.prog3.hotels.data.springdata;
import be.kdg.prog3.hotels.domain.Guest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

// Spring Data JPA repository that automatically provides CRUD and query methods
@Repository       // Marks this interface as persistence layer component managed by Spring
public interface SpringDataGuestRepository extends JpaRepository<Guest, Long> {

    // Query Method: VIP guests ( SELECT g FROM Guest g WHERE g.vip = true )
    List<Guest> findByVipTrue();

    // Query Method: Search by name ( SELECT g FROM Guest g WHERE lower(g.fullName) LIKE lower('%text%') )
    List<Guest> findByFullNameContainingIgnoreCase(String text);

    // Custom @Query: (JPQL query uses the size of the rooms collection to find guests who booked multiple rooms)
    @Query("""
           SELECT g
           FROM Guest g
           WHERE size(g.rooms) >= :minRooms
           """)
    List<Guest> findGuestsWithMoreThanRooms(@Param("minRooms") int minRooms);


    // Required for: getGuestsByRoom(roomNumber) -  returns guests assigned to a specific room
    @Query("""
           SELECT g
           FROM Guest g
           JOIN g.rooms r
           WHERE r.number = :roomNumber
           """)
    List<Guest> findByRoom(@Param("roomNumber") int roomNumber);
}
