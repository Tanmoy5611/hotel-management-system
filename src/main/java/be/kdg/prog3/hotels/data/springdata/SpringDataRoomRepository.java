package be.kdg.prog3.hotels.data.springdata;

import be.kdg.prog3.hotels.domain.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataRoomRepository extends JpaRepository<Room, Integer> {
}