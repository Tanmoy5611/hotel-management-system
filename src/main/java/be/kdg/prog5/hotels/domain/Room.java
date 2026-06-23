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
        // Speeds up queries that load rooms for one hotel or check room numbers inside a hotel
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
    @Column(name = "type", nullable = false) // Room type is mandatory domain data, so we enforce NOT NULL at DB level
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
    @JoinColumn(name = "hotel_id", nullable = false)                          // specifies the foreign key column in the database
    private Hotel hotel;


    // Instead of @ManyToMany Guest, using an intermediate entity
    // Room owns Stay lifecycle
    @OneToMany(mappedBy = "room",
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE},
            orphanRemoval = true)
    private Set<Stay> stays = new HashSet<>();

    // Required empty constructor for entity instantiation
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

    public Set<Stay> getStays() {
        return Collections.unmodifiableSet(stays); // Aggregate collections should not be modified directly from outside
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

    // check if THIS room is available
    public boolean isAvailable(LocalDate checkIn, LocalDate checkOut) {

        // basic safety (optional but good)
        if (checkIn == null || checkOut == null) {
            return true; // no dates -> assume available
        }

        for (Stay stay : this.stays) {

            // Overlap logic: If new booking starts before existing checkout
            // AND ends after existing checkin then conflict
            boolean overlaps =
                    checkIn.isBefore(stay.getCheckOutDate()) &&
                            checkOut.isAfter(stay.getCheckInDate());

            if (overlaps) {
                return false;  // room is NOT available
            }
        }

        return true;  // no conflicts then available
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

        // Overlap check of the dates with the existing isAvailable() method
        if (!this.isAvailable(checkIn, checkOut)) {
            throw new BookingException("booking.overlap.not.allowed");
        }

        Stay stay = new Stay(this, guest, checkIn, checkOut);
        stays.add(stay);
    }

    // Removes a booking by Stay id while keeping Stay ownership inside the Room aggregate
    public boolean removeStayById(Long stayId) {
        return stays.removeIf(stay -> Objects.equals(stay.getId(), stayId));
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
