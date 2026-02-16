package be.kdg.prog5.hotels.data;

import be.kdg.prog5.hotels.domain.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository       // this interface as a Spring-managed repository bean
public interface SpringDataHotelRepository extends JpaRepository<Hotel, Long> {
    // Spring gives ALL CRUD methods automatically

    // Aggregate Root Queries

    // Business identifier lookup ( Used in URLs: /hotels/{hotelId} )
    Optional<Hotel> findByHotelId(String hotelId);

    // to prevent N+1 queries in the hotel detail page
    @Query("""
    SELECT DISTINCT h
    FROM Hotel h
    LEFT JOIN FETCH h.rooms r
    LEFT JOIN FETCH r.stays s
    LEFT JOIN FETCH s.guest
    WHERE h.hotelId = :hotelId
""")
    Optional<Hotel> findByHotelIdWithAggregate(@Param("hotelId") String hotelId);

    /* Search hotels where name contains the given text, ignoring case
           Spring automatically converts this into:
           SELECT * FROM hotels WHERE LOWER(name) LIKE LOWER('%text%')   */
    List<Hotel> findByNameContainingIgnoreCase(String text);

    List<Hotel> findByStarsGreaterThanEqual(int minStars);

    // to check if there is already a hotel
    boolean existsByHotelId(String hotelId);
}