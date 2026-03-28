package be.kdg.prog5.hotels.web.config;

import be.kdg.prog5.hotels.data.SpringDataGuestRepository;
import be.kdg.prog5.hotels.data.SpringDataApplicationUserRepository;
import be.kdg.prog5.hotels.domain.ApplicationUser;
import be.kdg.prog5.hotels.domain.Guest;
import be.kdg.prog5.hotels.domain.VIPGuest;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Configuration
@Profile("!test") //  prevent test data from interfering with production
public class UserSeeder {

    // runs automatically when the application starts
    @Bean
    CommandLineRunner seedUsers(SpringDataApplicationUserRepository userRepository,
                                SpringDataGuestRepository guestRepository,
                                PasswordEncoder passwordEncoder) {

        return args -> {

            // only seed users if the table is empty
            if (userRepository.count() == 0) {

                // create default admin account
                ApplicationUser admin = new ApplicationUser(
                        "admin@hotelapp.com",
                        passwordEncoder.encode("admin123"),
                        "ADMIN"
                );

                // create normal applicationUser account
                ApplicationUser applicationUser = new ApplicationUser(
                        "applicationUser@hotelapp.com",
                        passwordEncoder.encode("user123"),
                        "USER"
                );

                // save both users in database
                userRepository.save(admin);
                userRepository.save(applicationUser);


                //  Seed Guests

                Guest g1 = new Guest(
                        "John Smith",
                        LocalDate.of(1990,5,10),
                        "john@email.com",
                        "https://i.pravatar.cc/150?img=1"

                );
                g1.setOwner(applicationUser);

                VIPGuest g2 = new VIPGuest(
                        "Alice Brown",
                        LocalDate.of(1988,3,22),
                        "alice@email.com",
                        "https://i.pravatar.cc/150?img=2",
                        new BigDecimal("20")
                );
                g2.setOwner(admin);


                guestRepository.save(g1);
                guestRepository.save(g2);
            }
        };
    }
}