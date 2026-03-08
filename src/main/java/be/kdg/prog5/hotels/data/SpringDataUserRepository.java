package be.kdg.prog5.hotels.data;


import be.kdg.prog5.hotels.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// Provides basic CRUD operations automatically (save, findAll, delete, etc)
public interface SpringDataUserRepository extends JpaRepository<User, Long> {

    // Custom query method to find a user by email
    Optional<User> findByEmail(String email);

}