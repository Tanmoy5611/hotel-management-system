package be.kdg.prog5.hotels.web.config;

import be.kdg.prog5.hotels.data.SpringDataUserRepository;
import be.kdg.prog5.hotels.domain.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class UserSeeder {

    // runs automatically when the application starts
    @Bean
    CommandLineRunner seedUsers(SpringDataUserRepository userRepository,
                                PasswordEncoder passwordEncoder) {

        return args -> {

            // only seed users if the table is empty
            if (userRepository.count() == 0) {

                // create default admin account
                User admin = new User(
                        "admin@hotelapp.com",
                        passwordEncoder.encode("password"),
                        "ADMIN"
                );

                // create normal user account
                User user = new User(
                        "user@hotelapp.com",
                        passwordEncoder.encode("password"),
                        "USER"
                );

                // save both users in database
                userRepository.save(admin);
                userRepository.save(user);
            }
        };
    }
}