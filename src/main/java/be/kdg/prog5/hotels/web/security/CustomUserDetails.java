package be.kdg.prog5.hotels.web.security;

import be.kdg.prog5.hotels.domain.ApplicationUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

//  adapts the ApplicationUser entity so Spring Security can understand it
public class CustomUserDetails implements UserDetails {

    // the actual applicationUser from the database
    private final ApplicationUser applicationUser;

    // constructor receives the ApplicationUser entity
    public CustomUserDetails(ApplicationUser applicationUser) {
        this.applicationUser = applicationUser;
    }

    // return the role of the applicationUser (STAFF or ADMIN)
    // Spring Security expects roles to start with "ROLE_"
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + applicationUser.getRole().name()));
    }

    // return the encrypted password stored in the database
    @Override
    public String getPassword() {
        return applicationUser.getPassword();
    }

    // use email as the username for login
    @Override
    public String getUsername() {
        return applicationUser.getEmail();
    }

    // account expiration (not used yet)
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // account locking (not implemented yet)
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // credentials expiration (not used yet)
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // applicationUser is always enabled
    @Override
    public boolean isEnabled() {
        return true;
    }
}