package be.kdg.prog5.hotels.data;


import be.kdg.prog5.hotels.domain.ApplicationUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// Provides basic CRUD operations automatically (save, findAll, delete, etc)
public interface SpringDataApplicationUserRepository extends JpaRepository<ApplicationUser, Long> {

    // Custom query method to find a user by email
    Optional<ApplicationUser> findByEmail(String email);

}