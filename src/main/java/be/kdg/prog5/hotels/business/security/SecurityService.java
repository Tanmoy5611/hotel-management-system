package be.kdg.prog5.hotels.business.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {

    // Returns the logged in email or null when the request is anonymous
    public String getLoggedInUsername() {
        Authentication auth = getAuthentication();

        if (auth == null || auth.getName() == null || auth.getName().equals("anonymousUser")) {
            return null;
        }

        return auth.getName();
    }

    // Used by services that must behave differently for customers
    public boolean isCustomer() {
        return hasRole("ROLE_CUSTOMER");
    }

    // Staff and admin may see internal booking data
    public boolean isStaffOrAdmin() {
        return hasRole("ROLE_STAFF") || hasRole("ROLE_ADMIN");
    }

    // Role check stays in one place so services do not repeat Spring Security code
    private boolean hasRole(String role) {
        Authentication auth = getAuthentication();

        if (auth == null) {
            return false;
        }

        return auth.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals(role));
    }

    // Small wrapper keeps SecurityContextHolder access out of the methods above
    private Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}