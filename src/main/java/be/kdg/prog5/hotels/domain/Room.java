package be.kdg.prog5.hotels.domain;

import be.kdg.prog5.hotels.business.exceptions.BookingException;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Entity
@Table(
        name = "rooms",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"hotel_id", "number"}
        ),
        indexes = @Index(name = "idx_rooms_hotel_id", columnList = "hotel_id")
)

// Attributes of Room class
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "number", nullable = false)
    private int number;

    @Enumerated(EnumType.STRING)    // stores enum values safely as readable strings
    @Column(name = "type")
    private RoomType type;

    @Column(name = "price_per_night", nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerNight;            // precision = 10 = total digits and scale = 2 = digits after decimal

    @Column(name = "sea_view")
    private boolean seaView;

    @Column(name = "photo_url")
    private String photoUrl;

    @Column(length = 1000)
    private String description;

    /// many-to-one - Room belongs to Hotel (aggregate boundary)
    // Default for ManyToOne is EAGER; must be changed to LAZY
    @ManyToOne(fetch = FetchType.LAZY, optional = false)    // represents a foreign-key (rooms.hotel_id) relationship where many rooms belong to one hotel
    @JoinColumn(name = "hotel_id", nullable = false)                          // specifies the foreign key column in the database.
    private Hotel hotel;


    // Instead of @ManyToMany Guest, using an intermediate entity
    // Room owns Stay lifecycle
    @OneToMany(mappedBy = "room",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private Set<Stay> stays = new HashSet<>();

    // Required empty constructor for entity instantiation.
    protected Room() {
    }

    // Constructor
    public Room(int number, RoomType type, BigDecimal pricePerNight, boolean seaView, String photoUrl, String description) {
        this.number = number;
        this.type = type;
        this.pricePerNight = pricePerNight;
        this.seaView = seaView;
        this.photoUrl = photoUrl;
        this.description = description;
    }

    // getters to access attributes
    public Long getId() {
        return id;
    }
    public int getNumber() {
        return number;
    }
    public RoomType getType() {
        return type;
    }
    public BigDecimal getPricePerNight() {
        return pricePerNight;
    }
    public boolean isSeaView() {
        return seaView;
    }
    public String getPhotoUrl() {
        return photoUrl;
    }
    public String getDescription() {
        return description;
    }
    public Hotel getHotel() {
        return hotel;
    }

    // Avoid lazy loading collections in UI. Return unmodifiable view.
    public Set<Stay> getStays() {
        return Collections.unmodifiableSet(stays);
    }

    // Setters
    public void setNumber(int number) {
        this.number = number;
    }
    public void setType(RoomType type) {
        this.type = type;
    }
    public void setPricePerNight(BigDecimal pricePerNight) {
        this.pricePerNight = pricePerNight;
    }
    public void setSeaView(boolean seaView) {
        this.seaView = seaView;
    }
    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    // Only package/domain can assign hotel
    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }

    // ROOM creates its own Stay
    public void addGuest(Guest guest, LocalDate checkIn, LocalDate checkOut) {

        if (checkIn == null || checkOut == null) {
            throw new BookingException("booking.dates.required");
        }

        if (!checkOut.isAfter(checkIn)) {
            throw new BookingException("booking.checkout.after.checkin");
        }

        if (checkIn.isBefore(LocalDate.now())) {
            throw new BookingException("booking.past.not.allowed");
        }

        // Overlap check of the dates
        boolean overlaps = stays.stream().anyMatch(existing ->
                checkIn.isBefore(existing.getCheckOutDate()) &&
                        checkOut.isAfter(existing.getCheckInDate())
        );

        if (overlaps) {
            throw new BookingException("booking.overlap.not.allowed");
        }

        Stay stay = new Stay(this, guest, checkIn, checkOut);
        stays.add(stay);
    }

    public void removeStay(Stay stay) {
        stays.remove(stay);
    }

    @Override
    public String toString() {
        return String.format("#%d %s %s €%.2f",
                number,
                type,
                (seaView ? "(sea)" : ""),
                pricePerNight);
    }
}