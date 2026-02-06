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
    @Column(name = "id", nullable = false)
    private String id;           //  Business identifier (used in URLs, e.g. /hotels/hilton-antwerp)

    @Column(name = "name", nullable = false)
    private String name;         // hotel name (required)

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String country;

    @Column(name = "opened_on")
    private LocalDate openedOn;   // opening date of the hotel

    @Column(name = "stars")
    private int stars;             // star rating (1–5)

    @Column(name = "has_spa")
    private boolean hasSpa;       // hotel has a spa or not

    @Column(name = "image_url")
    private String imageUrl;      // URL to the hotel's image

    @Column(length = 4000)
    private String description;

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
    public Hotel(String id, String name, String city, String country, LocalDate openedOn, int stars, boolean hasSpa, String imageUrl, String description) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.country = country;
        this.openedOn = openedOn;
        this.stars = stars;
        this.hasSpa = hasSpa;
        this.imageUrl = imageUrl;
        this.description = description;
    }

    // Getters to access attributes
    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getCity() {
        return city;
    }
    public String getCountry() {
        return country;
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
    public String getDescription() {
        return description;
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
    public void setCity(String city) {
        this.city = city;
    }
    public void setCountry(String country) {
        this.country = country;
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
    public void setDescription(String description) {
        this.description = description;
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
        return name + " (" + city + ", " + country + ") "
                + stars + "★ spa=" + hasSpa + "Description: " + description;
    }
}
