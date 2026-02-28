package be.kdg.prog5.hotels.data;

import be.kdg.prog5.hotels.domain.Stay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataStayRepository extends JpaRepository<Stay, Long> {
    void deleteByGuest_Id(Long guestId);
}
