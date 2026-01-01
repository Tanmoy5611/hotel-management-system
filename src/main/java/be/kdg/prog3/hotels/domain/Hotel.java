package be.kdg.prog3.hotels.domain;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity                              // JPA entity - this class becomes a table in the DB
@Table(name = "hotels")              // map this entity to "hotels" table

// Attributes of Hotel class
public class Hotel {

    @Id
    @Column(name = "id")
    private String id;           //  used a String id because it's easier for URLs

    @Column(name = "name", nullable = false)
    private String name;         // hotel name (required)

    @Column(name = "opened_on")
    private LocalDate openedOn;   // opening date of the hotel

    @Column(name = "stars")
    private int stars;             // star rating (1–5)

    @Column(name = "has_spa")
    private boolean hasSpa;       // hotel has a spa or not

    @Column(name = "image_url")
    private String imageUrl;      // URL to the hotel's image

    // Create a list - Each hotel has many rooms- 1 hotel → many rooms (inverse side)
    @OneToMany(mappedBy = "hotel",           // the owning side is Room.hotel
            cascade = CascadeType.ALL,       // remove rooms when hotel is deleted
            orphanRemoval = true)            // delete room if removed from the list
    // This automatically deletes Rooms when Hotel is deleted.
    private List<Room> rooms = new ArrayList<>();

    // JPA needs a empty constructor (Hibernate uses this to load data)
    protected Hotel() {
    }

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


    // helper method to keep both sides of the relationship in sync
    public void addRoom(Room room) {
        if (!rooms.contains(room)) {
            rooms.add(room);
        }
        // helper method to keep both sides of the relationship in sync
        if (room.getHotel() != this) {
            room.setHotel(this);          // Bidirectional relationship
        }
    }

    // remove room and also break the relationship correctly
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
