package be.kdg.prog5.hotels.web.security;

import be.kdg.prog5.hotels.business.customer.CustomerService;
import be.kdg.prog5.hotels.business.user.ApplicationUserService;
import be.kdg.prog5.hotels.domain.ApplicationUser;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// Used by Spring Security to load staff, admin, and customer accounts
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final ApplicationUserService applicationUserService;
    private final CustomerService customerService;

    public CustomUserDetailsService(ApplicationUserService applicationUserService,
                                    CustomerService customerService) {
        this.applicationUserService = applicationUserService;
        this.customerService = customerService;
    }

    // Spring Security calls this method during login
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        // Admin and staff accounts are stored as ApplicationUser
        ApplicationUser applicationUser = applicationUserService.findByEmail(email).orElse(null);
        if (applicationUser != null) {
            return new CustomUserDetails(applicationUser);
        }

        // Customer accounts use the Customer entity but still get a normal Spring Security user
        return customerService.findByEmail(email)
                .map(customer -> User.withUsername(customer.getProfile().getEmail())
                        .password(customer.getPassword())
                        .roles("CUSTOMER")
                        .disabled(!customer.isActive())
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("Account not found"));
    }
}