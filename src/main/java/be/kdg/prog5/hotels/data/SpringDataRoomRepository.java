package be.kdg.prog5.hotels.data;

import be.kdg.prog5.hotels.domain.Hotel;
import be.kdg.prog5.hotels.domain.Room;
import be.kdg.prog5.hotels.domain.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

// repository to talk to the database to fetch and store data in the persistence layer (No Business Logic)
@Repository
public interface SpringDataRoomRepository extends JpaRepository<Room, Long> {

    // Filtered list (rooms page)
    @Query("""
            SELECT DISTINCT r
            FROM Room r
            JOIN FETCH r.hotel
            WHERE (:type IS NULL OR r.type = :type)
              AND (:seaView IS NULL OR r.seaView = :seaView)
              AND (:maxPrice IS NULL OR r.pricePerNight <= :maxPrice)
    """)
    List<Room> findFilteredRooms(
            @Param("type") RoomType type,
            @Param("seaView") Boolean seaView,
            @Param("maxPrice") BigDecimal maxPrice
    );

    // Search by room number (rooms.html)
    @Query("""
        SELECT DISTINCT r
        FROM Room r
        JOIN FETCH r.hotel
        WHERE r.number = :number
    """)
    List<Room> findByNumberWithHotel(@Param("number") int number);

    // Used by guests pages where shown room + hotel (Rooms of one guest)
    @Query("""
            SELECT DISTINCT r
            FROM Stay s
            JOIN s.room r
            JOIN FETCH r.hotel
            WHERE s.guest.id = :guestId
    """)
    List<Room> findRoomsByGuestIdWithHotel(@Param("guestId") Long guestId);

    // All rooms
    @Query("""
            SELECT DISTINCT r
            FROM Room r
            JOIN FETCH r.hotel
    """)
    List<Room> findAllWithHotel();

    // Rooms of one hotel (hotel detail page)
    @Query("""
            SELECT DISTINCT r
            FROM Room r
            JOIN FETCH r.hotel
            WHERE r.hotel.hotelId = :hotelId
    """)
    List<Room> findByHotelIdWithHotel(@Param("hotelId") String hotelId);

    // Room detail page (room + hotel + stays + guests) aggregate root load
    @Query("""
            SELECT DISTINCT r
            FROM Room r
            JOIN FETCH r.hotel
            LEFT JOIN FETCH r.stays s
            LEFT JOIN FETCH s.guest
            WHERE r.id = :roomId
    """)
    Optional<Room> findByIdWithHotelAndGuests(@Param("roomId") Long roomId);

    /// For Home page
    // Cheapest room with JOIN FETCH and explicit Query
    @Query("SELECT r FROM Room r JOIN FETCH r.hotel ORDER BY r.pricePerNight ASC LIMIT 4")
    List<Room> findTop4ByOrderByPricePerNightAsc();

    // Most expensive rooms with JOIN FETCH and explicit Query
    @Query("SELECT r FROM Room r JOIN FETCH r.hotel ORDER BY r.pricePerNight DESC LIMIT 4")

    List<Room> findTop4ByOrderByPricePerNightDesc();
    // Tp prevent N+1: Rooms with most bookings
    @Query("""
        SELECT r FROM Room r 
        JOIN FETCH r.hotel h
        LEFT JOIN r.stays s 
        GROUP BY r.id, h.id 
        ORDER BY COUNT(s) DESC
    """)
    List<Room> findTopPickedRooms();

    boolean existsByHotelAndNumber(Hotel hotel, int number);

}