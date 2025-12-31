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
    private EntityManager em;    // EntityManager is a JPA tool to talk to the database

    // find all hotels
    @Override
    public List<Hotel> findAll() {
        // JPQL query: "Hotel" is the ENTITY name (not table name)
        // SELECT h FROM Hotel h then fetch all Hotel objects from DB
        return em.createQuery("SELECT h FROM Hotel h", Hotel.class)
                .getResultList();
    }

    // save or update hotel
    @Override
    public Hotel save(Hotel hotel) {
        // If the hotel already exists, update it (merge)
        if (em.find(Hotel.class, hotel.getId()) != null) {
            return em.merge(hotel);    // merge is update the existing row
        } else {
            em.persist(hotel);         // persist is insert new row
            return hotel;
        }
    }

    // find hotel by id
    @Override
    public Hotel findHotelById(String id) {
        // em.find() = SELECT * FROM hotels WHERE id = ?
        return em.find(Hotel.class, id);
    }

    // Delete hotel by id
    @Override
    public void delete(String id) {
        Hotel h = em.find(Hotel.class, id);
        if (h != null) em.remove(h);   // remove(entity) = DELETE FROM hotels WHERE id = ?
    }
}