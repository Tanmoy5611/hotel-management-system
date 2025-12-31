package be.kdg.prog3.hotels.data.jpa;

import be.kdg.prog3.hotels.data.RoomRepository;
import be.kdg.prog3.hotels.domain.Room;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
@Profile({"jpa", "dev", "prod"})
@Transactional
public class JpaRoomRepository implements RoomRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Room> findAll() {
        return em.createQuery("SELECT r FROM Room r", Room.class)
                .getResultList();
    }

    @Override
    public Room save(Room room) {
        if (em.find(Room.class, room.getNumber()) != null) {
            return em.merge(room);
        } else {
            em.persist(room);
            return room;
        }
    }

    @Override
    public Room findById(int number) {
        return em.find(Room.class, number);
    }

    @Override
    public List<Room> findByHotel(String hotelId) {
        return em.createQuery("""
                        SELECT r FROM Room r
                        WHERE r.hotel.id = :hotelId
                        """, Room.class)
                .setParameter("hotelId", hotelId)
                .getResultList();
    }

    @Override
    public List<Room> findByGuest(long guestId) {
        return em.createQuery("""
                        SELECT r FROM Room r
                        JOIN r.guests g
                        WHERE g.id = :gid
                        """, Room.class)
                .setParameter("gid", guestId)
                .getResultList();
    }

    @Override
    public void delete(int number) {
        Room r = em.find(Room.class, number);
        if (r != null) em.remove(r);
    }
}