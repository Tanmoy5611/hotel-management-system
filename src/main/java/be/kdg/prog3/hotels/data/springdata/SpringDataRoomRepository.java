package be.kdg.prog3.hotels.data.springdata;

import be.kdg.prog3.hotels.domain.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpringDataRoomRepository extends JpaRepository<Room, Long> {

    @Query("""
        select r
        from Room r
        join r.guests g
        where g.id = :guestId
    """)
    List<Room> findRoomsByGuestId(Long guestId);
}