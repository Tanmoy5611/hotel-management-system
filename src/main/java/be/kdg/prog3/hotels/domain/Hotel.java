package be.kdg.prog3.hotels.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "hotels")

// Attributes of Hotel class
public class Hotel {

    @Id
    @Column(name = "id")
    private String id;           // URL safe identifier for /hotels/{id}

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "opened_on")
    private LocalDate openedOn;

    @Column(name = "stars")
    private int stars;

    @Column(name = "has_spa")
    private boolean hasSpa;

    @Column(name = "image_url")
    private String imageUrl;

    // Create a list - Each hotel has many rooms- 1 hotel → many rooms (inverse side)
    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
    // This automatically deletes Rooms when Hotel is deleted.
    private List<Room> rooms = new ArrayList<>();

    // --- JPA requires a no-arg constructor ---
    protected Hotel() {
    }

    // Default constructor for Spring and Thymeleaf forms to create objects
    //  public Hotel() {
    //  }


    // Constructor
    public Hotel(String id, String name, LocalDate openedOn, int stars, boolean hasSpa, String imageUrl) {
        this.id = id;
        this.name = name;
        this.openedOn = openedOn;
        this.stars = stars;
        this.hasSpa = hasSpa;
        this.imageUrl = imageUrl;
    }


    // Getters to access attributes
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDate getOpenedOn() {
        return openedOn;
    }

    public int getStars() {
        return stars;
    }

    public boolean isHasSpa() {
        return hasSpa;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    // Setters
    public void setId(String id) {
        this.id = id;

    }

    public void setName(String name) {
        this.name = name;

    }

    public void setOpenedOn(LocalDate openedOn) {
        this.openedOn = openedOn;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }


    public void setHasSpa(boolean hasSpa) {
        this.hasSpa = hasSpa;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


    // Method to add a room to the hotel
    public void addRoom(Room room) {
        if (!rooms.contains(room)) {
            rooms.add(room);
        }
        if (room.getHotel() != this) {
            room.setHotel(this);          // Bidirectional relationship
        }
    }

    // Method to remove a room from the hotel
    public void removeRoom(Room room) {
        rooms.remove(room);
        if (room.getHotel() == this) {
            room.setHotel(null);
        }
    }

    // Override toString method to print hotel details
    @Override
    public String toString() {
        return name + " [" + id + "'" + stars + "★, spa=" + hasSpa + ", opened=" + openedOn + "]";

    }

}
