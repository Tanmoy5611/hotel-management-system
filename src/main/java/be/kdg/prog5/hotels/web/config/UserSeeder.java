package be.kdg.prog5.hotels.web.config;

import be.kdg.prog5.hotels.config.AppConstants;
import be.kdg.prog5.hotels.data.SpringDataGuestRepository;
import be.kdg.prog5.hotels.data.SpringDataApplicationUserRepository;
import be.kdg.prog5.hotels.domain.ApplicationUser;
import be.kdg.prog5.hotels.domain.Guest;
import be.kdg.prog5.hotels.domain.RoleType;
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

    private static final String DEFAULT_USER_EMAIL = "applicationUser@hotelapp.com";
    private static final String DEFAULT_ADMIN_PASSWORD = "admin123";
    private static final String DEFAULT_USER_PASSWORD = "user123";

    // runs automatically when the application starts
    @Bean
    CommandLineRunner seedUsers(SpringDataApplicationUserRepository userRepository,
                                SpringDataGuestRepository guestRepository,
                                PasswordEncoder passwordEncoder) {

        return args -> {

            ApplicationUser admin = findOrCreateUser(
                    userRepository,
                    passwordEncoder,
                    AppConstants.PROTECTED_ADMIN_EMAIL,
                    DEFAULT_ADMIN_PASSWORD,
                    RoleType.ADMIN
            );

            ApplicationUser applicationUser = findOrCreateUser(
                    userRepository,
                    passwordEncoder,
                    DEFAULT_USER_EMAIL,
                    DEFAULT_USER_PASSWORD,
                    RoleType.STAFF
            );

            // Only seed demo guests when the guest table is empty
            if (guestRepository.count() == 0) {
                seedDemoGuests(guestRepository, admin, applicationUser);
            }
        };
    }

    private ApplicationUser findOrCreateUser(SpringDataApplicationUserRepository userRepository,
                                             PasswordEncoder passwordEncoder,
                                             String email,
                                             String rawPassword,
                                             RoleType role) {
        return userRepository.findByEmail(email)
                .orElseGet(() -> userRepository.save(new ApplicationUser(
                        email,
                        passwordEncoder.encode(rawPassword),
                        role
                )));
    }

    private void seedDemoGuests(SpringDataGuestRepository guestRepository,
                                ApplicationUser admin,
                                ApplicationUser applicationUser) {
        // Seed a regular guest owned by the normal user
        Guest g1 = new Guest(
                "John Smith",
                LocalDate.of(1990, 5, 10),
                "john@email.com",
                "https://i.pravatar.cc/150?img=1"
        );
        g1.setOwner(applicationUser);

        // Seed a VIP guest owned by the protected admin
        VIPGuest g2 = new VIPGuest(
                "Alice Brown",
                LocalDate.of(1988, 3, 22),
                "alice@email.com",
                "https://i.pravatar.cc/150?img=2",
                new BigDecimal("20")
        );
        g2.setOwner(admin);

        guestRepository.save(g1);
        guestRepository.save(g2);
    }
}