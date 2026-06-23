package be.kdg.prog5.hotels.web.security;

import be.kdg.prog5.hotels.business.ApplicationUserService;
import be.kdg.prog5.hotels.domain.ApplicationUser;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// used by Spring Security to load users from the database
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final ApplicationUserService applicationUserService;

    public CustomUserDetailsService(ApplicationUserService applicationUserService) {
        this.applicationUserService = applicationUserService;
    }

    // Spring Security calls this method during login
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        // find the applicationUser by email
        ApplicationUser applicationUser = applicationUserService.findByEmail(email)
                // if applicationUser not found then throw exception
                .orElseThrow(() -> new UsernameNotFoundException("ApplicationUser not found"));

        // convert ApplicationUser entity into CustomUserDetails object
        return new CustomUserDetails(applicationUser);
    }
}