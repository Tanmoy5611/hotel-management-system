package be.kdg.prog5.hotels.domain;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

// representing a user that can log into the HotelBooking application
@Entity
@Table(name = "application_user")    // Custom table name
public class ApplicationUser {

    // Primary key of the user
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String password;

    private String role;

    // ApplicationUser owns Guest
    // ApplicationUser (1) <--> (many) Guest
    @OneToMany(mappedBy = "owner")
    private List<Guest> guests = new ArrayList<>();


    public ApplicationUser() {
    }

    public ApplicationUser(String email, String password, String role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // getters & setters
    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<Guest> getGuests() {
        return guests;
    }

    public void setGuests(List<Guest> guests) {
        this.guests = guests;
    }
}