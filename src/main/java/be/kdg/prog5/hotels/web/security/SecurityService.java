package be.kdg.prog5.hotels.web.security;

import be.kdg.prog5.hotels.data.SpringDataApplicationUserRepository;
import be.kdg.prog5.hotels.domain.ApplicationUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

// retrieves the currently logged-in user for ServiceImpl (Logging Activity)
@Service
public class SecurityService {

    private final SpringDataApplicationUserRepository userRepository;

    public SecurityService(SpringDataApplicationUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Returns null instead of throwing exception (business logic (services/tests) never break because of security)
    public ApplicationUser getLoggedInUserSafe() {

        // Get authentication object from Spring Security context
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // If no authentication OR anonymous user then return null safely
        if (auth == null || auth.getName() == null || auth.getName().equals("anonymousUser")) {
            return null;
        }

        // Fetch full ApplicationUser from db using email (username)
        // If not found - return null (safe, no exception)
        return userRepository.findByEmail(auth.getName()).orElse(null);
    }
}