package be.kdg.prog3.hotels.domain;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

// Attributes of Guest class
public class Guest {
    private long id;
    private String email;
    private boolean vip;
    private String avatarUrl;
    private String fullName;
    private LocalDate dob;

    /// many-to-many with Room for storing a set of rooms booked
    private final Set<Room> rooms = new HashSet<>();

    // Default constructor for Spring and Thymeleaf forms to create objects
    public Guest() {

    }


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


    public Set<Room> getRooms() {
        return rooms;
    }

    // Method to add a room to guest's booking
    public void addRoom(Room room) {
        rooms.add(room);
    }


    // Override to string method
    @Override
    public String toString() {
        return fullName + " (vip=" + vip + ", dob=" + dob + ", email=" + email + ")";
    }

}
