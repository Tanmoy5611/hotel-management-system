package be.kdg.prog3.hotels.data.jpa;
import be.kdg.prog3.hotels.data.RoomRepository;
import be.kdg.prog3.hotels.domain.Room;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import java.util.List;

// JPA-based repository that handles Room persistence using EntityManager
@Repository                     // Marks this class as a persistence component and enables Spring exception translation
@Profile({"jpa", "dev", "prod"})
@Transactional
public class JpaRoomRepository implements RoomRepository {

    @PersistenceContext
    private EntityManager em;

    // Loads all Room entities using JPQL.
    @Override
    public List<Room> findAll() {
        return em.createQuery("SELECT r FROM Room r", Room.class)
                .getResultList();
    }

    // Finds a room by its primary key and save or update
    @Override
    public Room save(Room room) {
        if (room.getId() != null && em.find(Room.class, room.getId()) != null) {
            return em.merge(room);
        } else {
            em.persist(room);
            return room;
        }
    }

    // Find by PRIMARY KEY (Room.id)
    @Override
    public Room findById(Long id) {
        return em.find(Room.class, id);
    }

    // Retrieves rooms belonging to a specific hotel (many-to-one)
    @Override
    public List<Room> findByHotel(String hotelId) {
        return em.createQuery("""
                        SELECT r FROM Room r
                        WHERE r.hotel.id = :hotelId
                        """, Room.class)
                .setParameter("hotelId", hotelId)
                .getResultList();
    }

    // Retrieves rooms linked to a guest (many-to-many)
    @Override
    public List<Room> findByGuest(Long guestId) {
        return em.createQuery("""
                        SELECT r FROM Room r
                        JOIN r.guests g
                        WHERE g.id = :gid
                        """, Room.class)
                .setParameter("gid", guestId)
                .getResultList();
    }

    // Removes the room entity if it exists.
    @Override
    public void delete(Long id) {
        Room r = em.find(Room.class, id);
        if (r != null) em.remove(r);
    }
}