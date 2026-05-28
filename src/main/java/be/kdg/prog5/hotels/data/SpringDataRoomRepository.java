package be.kdg.prog5.hotels.data;

import be.kdg.prog5.hotels.domain.Hotel;
import be.kdg.prog5.hotels.domain.Room;
import be.kdg.prog5.hotels.domain.RoomType;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
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

    // All rooms
    @Query("""
            SELECT DISTINCT r
            FROM Room r
            JOIN FETCH r.hotel
    """)
    List<Room> findAllWithHotel();

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

    // Locks one room (pessimistic) row while a booking/cancellation changes its Stay collection
    // If two users book the same room at the same time, the first transaction keeps this lock
    // The second transaction waits, then reloads the stays and lets Room.addGuest reject overlaps
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT r
            FROM Room r
            WHERE r.id = :roomId
    """)
    Optional<Room> findByIdForUpdate(@Param("roomId") Long roomId);

    // Sorted stays - for room detail page (ORDER BY in DB, not in Java)
    @Query("""
            SELECT DISTINCT r
            FROM Room r
            JOIN FETCH r.hotel
            LEFT JOIN FETCH r.stays s
            LEFT JOIN FETCH s.guest
            WHERE r.id = :roomId
            ORDER BY s.checkInDate ASC
    """)
    Optional<Room> findByIdWithHotelAndGuestsSortedByCheckIn(@Param("roomId") Long roomId);

    /// For Home page
    // Cheapest room with JOIN FETCH and explicit Query
    @Query("""
        SELECT r FROM Room r
        JOIN FETCH r.hotel
        ORDER BY r.pricePerNight ASC
    """)
    List<Room> findCheapestRooms(Pageable pageable);

    // Most expensive rooms with JOIN FETCH and explicit Query
    @Query("""
        SELECT r FROM Room r
        JOIN FETCH r.hotel
        ORDER BY r.pricePerNight DESC
    """)
    List<Room> findMostExpensiveRooms(Pageable pageable);

    // Tp prevent N+1 via JOIN FETCH + GROUP BY): Rooms with most bookings
    @Query("""
        SELECT r FROM Room r
        JOIN FETCH r.hotel h
        LEFT JOIN r.stays s
        GROUP BY r.id, h.id
        ORDER BY COUNT(s) DESC
    """)
    List<Room> findTopPickedRooms(Pageable pageable);

    // Search rooms by hotel name/city/country and optional room type
    // JOIN FETCH avoids N+1 for hotel, and stays are not fetched here because
    // search results do not need full Stay objects
    @Query("""
            SELECT DISTINCT r
            FROM Room r
            JOIN FETCH r.hotel h
            WHERE (LOWER(h.name) LIKE LOWER(CONCAT('%', :query, '%'))
                   OR LOWER(h.city) LIKE LOWER(CONCAT('%', :query, '%'))
                   OR LOWER(h.country) LIKE LOWER(CONCAT('%', :query, '%')))
              AND (:roomType IS NULL OR r.type = :roomType)
            """)
    List<Room> searchRooms(@Param("query") String query,
                           @Param("roomType") RoomType roomType);

    // Availability search needs stays loaded before Room.isAvailable() is called
    @Query("""
            SELECT DISTINCT r
            FROM Room r
            JOIN FETCH r.hotel h
            LEFT JOIN FETCH r.stays
            WHERE (LOWER(h.name) LIKE LOWER(CONCAT('%', :query, '%'))
                   OR LOWER(h.city) LIKE LOWER(CONCAT('%', :query, '%'))
                   OR LOWER(h.country) LIKE LOWER(CONCAT('%', :query, '%')))
              AND (:roomType IS NULL OR r.type = :roomType)
            """)
    List<Room> searchRoomsWithStays(@Param("query") String query,
                                    @Param("roomType") RoomType roomType);

    boolean existsByHotelAndNumber(Hotel hotel, int number);
}