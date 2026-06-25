package be.kdg.prog5.hotels.data;

import be.kdg.prog5.hotels.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

// Repository for customer login accounts
// The profile is fetched because the page usually needs the customer email
public interface SpringDataCustomerRepository extends JpaRepository<Customer, Long> {

    // Login and dashboard lookup by the email stored on the connected Guest profile
    @Query("SELECT c FROM Customer c JOIN FETCH c.profile WHERE LOWER(c.profile.email) = LOWER(:email)")
    Optional<Customer> findByProfileEmail(@Param("email") String email);

    // Admin user management needs every customer together with their profile email
    @Query("SELECT c FROM Customer c JOIN FETCH c.profile ORDER BY c.profile.email")
    List<Customer> findAllWithProfiles();
}