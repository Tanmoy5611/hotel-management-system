package be.kdg.prog3.hotels.data.springdata;
import be.kdg.prog3.hotels.domain.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository       // this interface as a Spring-managed repository bean
public interface SpringDataHotelRepository extends JpaRepository<Hotel, String> {
    // Spring gives us ALL CRUD methods automatically.

    /* Search hotels where name contains the given text, ignoring case
           Spring automatically converts this into:
           SELECT * FROM hotels WHERE LOWER(name) LIKE LOWER('%text%')   */
    List<Hotel> findByNameContainingIgnoreCase(String text);

    // Find hotels where stars >= given value
    // SELECT * FROM hotels WHERE stars >= ?
    List<Hotel> findByStarsGreaterThanEqual(int stars);


    // Find hotels with exact stars value
    // SELECT * FROM hotels WHERE stars = ?
    List<Hotel> findByStars(int stars);

    // Filter hotels by spa availability
    // SELECT * FROM hotels WHERE has_spa = ?
    List<Hotel> findByHasSpa(boolean hasSpa);

    // Search hotels opened after specific date
    // SELECT * FROM hotels WHERE opened_on > ?
    List<Hotel> findByOpenedOnAfter(LocalDate date);

    // Search hotels opened before specific date
    // SELECT * FROM hotels WHERE opened_on < ?
    List<Hotel> findByOpenedOnBefore(LocalDate date);


    // Custom JPQL query to find hotels where stars >= minStars.
    // JPQL runs on entities, not tables
    @Query("""
           SELECT h
           FROM Hotel h
           WHERE h.stars >= :minStars
           """)
    List<Hotel> findHotelsWithMinStars(@Param("minStars") int minStars);
}