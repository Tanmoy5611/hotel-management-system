package be.kdg.prog3.hotels.data.jpa;
import be.kdg.prog3.hotels.data.GuestRepository;
import be.kdg.prog3.hotels.domain.Guest;
import be.kdg.prog3.hotels.domain.Room;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import java.util.List;

// JPA-based repository that manages Guest entities using EntityManager to persist and ORM (JPQL not SQL)
@Repository                 // marks this class data-access component and enables Spring exception translation
@Profile({"jpa", "dev", "prod"})
@Transactional                  // Ensures database operations run inside a single transaction ( commit or rollback)
public class JpaGuestRepository implements GuestRepository {

    // Injects the JPA EntityManager (Core JPA API), which handles persistence automatically
    @PersistenceContext
    private EntityManager em;

    // Uses JPQL (uses entity not table names) to retrieve all Guest entities from the database
    @Override
    public List<Guest> findAll() {
        return em.createQuery("SELECT g FROM Guest g", Guest.class)
                .getResultList();
    }

    // If the guest already exists, update it using merge; otherwise insert it using persist
    @Override
    public Guest save(Guest guest) {
        if (guest.getId() != null) {
            return em.merge(guest);        // update
        } else {
            em.persist(guest);             // insert
            return guest;
        }
    }

    // finds a Guest by its primary key using EntityManager
    @Override
    public Guest findById(Long id) {
        return em.find(Guest.class, id);
    }

    // Uses JPQL JOIN to navigate the many-to-many relationship between guests and rooms
    @Override
    public List<Guest> findByRoom(Long roomId) {
        return em.createQuery("""
                        SELECT g FROM Guest g
                        JOIN g.rooms r
                        WHERE r.id = :roomId
                        """, Guest.class)
                .setParameter("roomId", roomId)
                .getResultList();
    }

    // Loads the entity first, then removes it using EntityManager
    @Override
    public void delete(Long id) {
        Guest g = em.find(Guest.class, id);
        if (g != null) em.remove(g);
    }

    @Override
    public void addGuestToRoom(Long guestId, Long roomId) {

        Guest guest = em.find(Guest.class, guestId);
        Room room = em.find(Room.class, roomId);

        if (guest == null || room == null) {
            throw new IllegalArgumentException("Guest or Room not found");
        }

        // OWNING SIDE
        room.getGuests().add(guest);

        // inverse side (optional but correct)
        guest.getRooms().add(room);
    }
}