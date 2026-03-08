package be.kdg.prog5.hotels.web.security;

import be.kdg.prog5.hotels.data.SpringDataUserRepository;
import be.kdg.prog5.hotels.domain.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// used by Spring Security to load users from the database
@Service
public class CustomUserDetailsService implements UserDetailsService {

    // repository to fetch users from the DB
    private final SpringDataUserRepository userRepository;

    public CustomUserDetailsService(SpringDataUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Spring Security calls this method during login
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        // find the user by email
        User user = userRepository.findByEmail(email)
                // if user not found then throw exception
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // convert User entity into CustomUserDetails object
        return new CustomUserDetails(user);
    }
}