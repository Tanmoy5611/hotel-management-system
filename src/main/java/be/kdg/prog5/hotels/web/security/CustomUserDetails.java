package be.kdg.prog5.hotels.web.security;

import be.kdg.prog5.hotels.domain.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

//  adapts the User entity so Spring Security can understand it
public class CustomUserDetails implements UserDetails {

    // the actual user from the database
    private final User user;

    // constructor receives the User entity
    public CustomUserDetails(User user) {
        this.user = user;
    }

    // return the role of the user (USER or ADMIN)
    // Spring Security expects roles to start with "ROLE_"
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
    }

    // return the encrypted password stored in the database
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    // use email as the username for login
    @Override
    public String getUsername() {
        return user.getEmail();
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

    // user is always enabled
    @Override
    public boolean isEnabled() {
        return true;
    }
}