package be.kdg.prog3.hotels.data.springdata;

import be.kdg.prog3.hotels.domain.Guest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SpringDataGuestRepository extends JpaRepository<Guest, Long> {

    // Query Method: VIP guests
    List<Guest> findByVipTrue();

    // Query Method: Search by name
    List<Guest> findByFullNameContainingIgnoreCase(String text);

    // Custom @Query: Guests with more than X booked rooms
    @Query("""
           SELECT g
           FROM Guest g
           WHERE size(g.rooms) >= :minRooms
           """)
    List<Guest> findGuestsWithMoreThanRooms(@Param("minRooms") int minRooms);


    // Required for: getGuestsByRoom(roomNumber)
    @Query("""
           SELECT g
           FROM Guest g
           JOIN g.rooms r
           WHERE r.number = :roomNumber
           """)
    List<Guest> findByRoom(@Param("roomNumber") int roomNumber);
}
