package be.kdg.prog3.hotels.data;
import be.kdg.prog3.hotels.domain.Hotel;
import java.util.List;

    // Repository interface for Hotel entity
    // It defines how service layer will communicate with the data layer.
    // Different implementations (in-memory, JDBC, JPA, Spring Data) will implement this

public interface HotelRepository {

    // InMemory = returns List
    // JDBC = executes "SELECT * FROM hotels"
    // JPA = entityManager.createQuery("from Hotel")
    List<Hotel> findAll();    // for retrieving all store hotels

    // InMemory = adds to List
    // JDBC = executes INSERT or UPDATE
    // JPA = em.persist() / em.merge()
    Hotel save(Hotel hotel);  // saves a new hotel to the data source

     // Returns a hotel by its ID (String).
     // ID is URL-safe (like "plaza-athenee-paris")
     // Used in hotel detail page like /hotels/{id}
    Hotel findHotelById(String id);

     // Cascade deletes rooms
     // In-memory - list.remove()
     // JDBC - DELETE FROM hotels WHERE id=?
     // JPA - em.remove()
    void delete(String id);
}