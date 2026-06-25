package be.kdg.prog5.hotels.web.security;

import be.kdg.prog5.hotels.data.SpringDataGuestRepository;
import be.kdg.prog5.hotels.domain.Guest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/* Used by @PreAuthorize to check if the logged-in user may manage a specific Guest.
- Spring calls this bean from a SpEL expression:
- @PreAuthorize("@guestAuthorizationService.canDeleteGuest(#guestId, authentication)")
- This helper is only needed for Guest because Guest has an owner (ApplicationUser) relationship.
- Guest deletion rule: the Guest owner OR an ADMIN may delete the Guest. BUT Room and Hotel deletion are different:
  they are admin-managed records, so simple; hasRole('ADMIN') security is enough there */

@Service
public class GuestAuthorizationService {

    private final SpringDataGuestRepository guestRepository;

    public GuestAuthorizationService(SpringDataGuestRepository guestRepository) {
        this.guestRepository = guestRepository;
    }

    @Transactional(readOnly = true)
    public boolean canDeleteGuest(Long guestId, Authentication authentication) {
        // Anonymous users are represented by Spring as authenticated=false or
        // AnonymousAuthenticationToken, so they must never pass ownership checks
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return false;
        }

        // Admin may manage every Guest, even when not the owner
        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            return true;
        }

        // STAFF may only delete Guests that they own
        return guestRepository.findById(guestId)
                .map(Guest::getOwner)
                .map(owner -> owner.getEmail().equals(authentication.getName()))
                .orElse(false);
    }
}