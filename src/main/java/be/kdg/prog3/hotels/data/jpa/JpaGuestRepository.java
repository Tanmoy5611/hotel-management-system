package be.kdg.prog3.hotels.data.jpa;

import be.kdg.prog3.hotels.data.GuestRepository;
import be.kdg.prog3.hotels.domain.Guest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Profile({"jpa", "dev", "prod"})
@Transactional
public class JpaGuestRepository implements GuestRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Guest> findAll() {
        return em.createQuery("SELECT g FROM Guest g", Guest.class)
                .getResultList();
    }

    @Override
    public Guest save(Guest guest) {
        if (guest.getId() != 0) {
            return em.merge(guest);        // update
        } else {
            em.persist(guest);             // insert
            return guest;
        }
    }

    @Override
    public Guest findById(long id) {
        return em.find(Guest.class, id);
    }

    @Override
    public List<Guest> findByRoom(int roomNumber) {
        return em.createQuery("""
                        SELECT g FROM Guest g
                        JOIN g.rooms r
                        WHERE r.number = :roomNum
                        """, Guest.class)
                .setParameter("roomNum", roomNumber)
                .getResultList();
    }

    @Override
    public void delete(long id) {
        Guest g = em.find(Guest.class, id);
        if (g != null) em.remove(g);
    }
}