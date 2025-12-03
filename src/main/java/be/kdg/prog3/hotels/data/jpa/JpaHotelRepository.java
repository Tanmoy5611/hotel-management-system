package be.kdg.prog3.hotels.data.jpa;

import be.kdg.prog3.hotels.data.HotelRepository;
import be.kdg.prog3.hotels.domain.Hotel;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Profile({"jpa", "dev", "prod"})
@Transactional
public class JpaHotelRepository implements HotelRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Hotel> findAll() {
        return em.createQuery("SELECT h FROM Hotel h", Hotel.class)
                .getResultList();
    }

    @Override
    public Hotel save(Hotel hotel) {
        if (em.find(Hotel.class, hotel.getId()) != null) {
            return em.merge(hotel);
        } else {
            em.persist(hotel);
            return hotel;
        }
    }

    @Override
    public Hotel findHotelById(String id) {
        return em.find(Hotel.class, id);
    }

    @Override
    public void delete(String id) {
        Hotel h = em.find(Hotel.class, id);
        if (h != null) em.remove(h);
    }
}