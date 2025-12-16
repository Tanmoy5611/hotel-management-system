package be.kdg.prog3.hotels.domain;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


// Attributes of Guest class
@Entity
@Table(name = "guests")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "guest_type")
@DiscriminatorValue("GUEST")
public class Guest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String email;
    private boolean vip;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "full_name")
    private String fullName;
    private LocalDate dob;

    /// many-to-many with Room for storing a set of rooms booked - inverse side
    @ManyToMany(mappedBy = "guests")
    private Set<Room> rooms = new HashSet<>();

    // Empty constructor
    protected Guest() {
    }

    // Default constructor for Spring and Thymeleaf forms to create objects
    // public Guest() {
    // }


    // Constructor
    public Guest(String fullName, LocalDate dob, String email, boolean vip, String avatarUrl) {
        this.fullName = fullName;
        this.dob = dob;
        this.email = email;
        this.vip = vip;
        this.avatarUrl = avatarUrl;

    }

    // getters
    public long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public LocalDate getDob() {
        return dob;
    }

    public String getEmail() {
        return email;
    }

    public boolean isVip() {
        return vip;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public Set<Room> getRooms() {
        return rooms;
    }


    // Setters
    public void setId(long id) {
        this.id = id;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;

    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setVip(boolean vip) {
        this.vip = vip;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;

    }


    // Method to add a room to guest's booking - Relationship helper
    public void addRoom(Room room) {
        rooms.add(room);
        room.getGuests().add(this);   // sync bidirectionally
    }

    public double getDiscountPercentage() {
        return 0;   // regular guests have no discount
    }


    // Override to string method
    @Override
    public String toString() {
        return fullName + " (vip=" + vip + ", dob=" + dob + ", email=" + email + ")";
    }

}
