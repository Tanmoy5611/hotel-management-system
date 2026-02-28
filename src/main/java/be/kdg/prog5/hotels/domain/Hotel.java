package be.kdg.prog5.hotels.domain;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.*;

@Entity                               // JPA entity - this class becomes a table in the DB
@Table(
        name = "hotels",
        indexes = {
                @Index(name = "idx_hotels_hotel_id", columnList = "hotel_id", unique = true)
                // enforcing uniqueness at database level and add an index to fast lookups and scalability
        }
)

// Attributes of Hotel class
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Technical primary key for database

    @Column(name = "hotel_id", nullable = false, unique = true, updatable = false)
    private String hotelId;           //  Business identifier (used in URLs,like /hotels/hilton-antwerp)

    @Column(name = "name", nullable = false)
    private String name;         // hotel name

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String country;

    @Column(name = "opened_on")
    private LocalDate openedOn;   // opening date of the hotel

    @Column(name = "stars", nullable = false)
    private int stars;             // star rating (1–5)

    @Column(name = "has_spa", nullable = false)
    private boolean hasSpa;       // hotel has a spa or not

    @Column(name = "image_url")
    private String imageUrl;      // URL to the hotel's image

    @Column(length = 4000)
    private String description;

    /// Aggregate Ownership - Hotel OWNS Rooms
    @OneToMany(
            mappedBy = "hotel",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<Room> rooms = new HashSet<>();  //  This removes MultipleBagFetchException

    // JPA needs a empty constructor (Hibernate uses this to load data)
    protected Hotel() {
    }

    // Constructor
    public Hotel(String hotelId, String name, String city, String country,
                 LocalDate openedOn, int stars, boolean hasSpa, String imageUrl,
                 String description) {
        this.hotelId = hotelId;
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
    public Long getId() {
            return id;}
    public String getHotelId() {
        return hotelId;
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
    public boolean hasSpa() {
        return hasSpa;
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public String getDescription() {
        return description;
    }

    // Avoid lazy loading collections in the UI;
    // Return an unmodifiable list to prevent direct manipulation without helper methods
    public Set<Room> getRooms() {
        return Collections.unmodifiableSet(rooms);
    }

    // Setters
    public void setHotelId(String hotelId) {
        this.hotelId = hotelId;
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

    // Aggregate Helper Methods

    // Add Room to this Hotel - Keeps both sides of relationship in sync
    public void addRoom(Room room) {
        rooms.add(room);
        room.setHotel(this);
    }

    // Remove Room from this Hotel.
    public void removeRoom(Room room) {
        rooms.remove(room);
        room.setHotel(null);
    }

    // Override toString method to print hotel details
    @Override
    public String toString() {
        return String.format("%s (%s, %s), %d★, spa=%b", name, city, country, stars, hasSpa);
    }
}