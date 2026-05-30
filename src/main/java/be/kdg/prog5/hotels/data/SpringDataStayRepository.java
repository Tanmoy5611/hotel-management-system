package be.kdg.prog5.hotels.data;

import be.kdg.prog5.hotels.domain.Stay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SpringDataStayRepository extends JpaRepository<Stay, Long> {
    // Deletes all stays for a guest before deleting the guest aggregate
    void deleteByGuest_Id(Long guestId);

    // Loads active bookings for the admin bookings page with all details needed by the table
    @Query("""
            SELECT s
            FROM Stay s
            JOIN FETCH s.guest g
            JOIN FETCH s.room r
            JOIN FETCH r.hotel
            WHERE s.checkOutDate >= :today
            ORDER BY s.checkInDate ASC, r.number ASC
            """)
    List<Stay> findCurrentBookingsWithDetails(@Param("today") LocalDate today);

    // Loads one booking with guest, room, and hotel before cancellation
    @Query("""
            SELECT s
            FROM Stay s
            JOIN FETCH s.guest g
            JOIN FETCH s.room r
            JOIN FETCH r.hotel
            WHERE s.id = :stayId
            """)
    Optional<Stay> findByIdWithBookingDetails(@Param("stayId") Long stayId);
}