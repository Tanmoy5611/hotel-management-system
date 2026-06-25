package be.kdg.prog5.hotels.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String password;

    // Admin can deactivate customer login without deleting the customer history
    @Column(nullable = false, columnDefinition = "boolean default true")
    private boolean active = true;

    // Customer personal data is reused from Guest so bookings still use the existing model
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "guest_id", nullable = false, unique = true)
    private Guest profile;

    // Required by JPA
    protected Customer() {
    }

    public Customer(String password, Guest profile) {
        this.password = password;
        this.profile = profile;
    }

    public Long getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public boolean isActive() {
        return active;
    }

    public Guest getProfile() {
        return profile;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}