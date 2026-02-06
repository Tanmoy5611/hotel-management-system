package be.kdg.prog3.hotels.domain;
import java.util.HashSet;
import java.util.Set;
import jakarta.persistence.*;

@Entity
@Table(
        name = "rooms",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"hotel_id", "number"}
        )
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

    @Column(name = "price_per_night")
    private double pricePerNight;

    @Column(name = "sea_view")
    private boolean seaView;

    @Column(name = "photo_url")
    private String photoUrl;

    @Column(length = 1000)
    private String description;

    //  Many rooms → one hotel
    @ManyToOne(optional = false)
    // represents a foreign-key (rooms.hotel_id) relationship where many rooms belong to one hotel
    @JoinColumn(name = "hotel_id")     // specifies the foreign key column in the database.
    private Hotel hotel;
    /// many-to-one

    // Many-to-Many: rooms_guests
    @ManyToMany
    //(fetch = FetchType.EAGER)   // models many-to-many relationship using a join table (Avoids LazyInitializationException for EAGER)
    @JoinTable(                            //  defines the join table and its foreign keys explicitly.
            name = "rooms_guests",
            joinColumns = @JoinColumn(name = "room_id"),      // FK - Room
            inverseJoinColumns = @JoinColumn(name = "guest_id")   // FK - Guest
    )

    private Set<Guest> guests = new HashSet<>();

    // Required empty constructor for entity instantiation.
    protected Room() {
    }

    // Constructor
    public Room(int number, RoomType type, double pricePerNight, boolean seaView, String photoUrl, String description) {
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
    public double getPricePerNight() {
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
    public Set<Guest> getGuests() {
        return guests;
    }

    // Setters

    public void setId(Long id) {
        this.id = id;
    }
    public void setNumber(int number) {
        this.number = number;
    }
    public void setType(RoomType type) {
        this.type = type;
    }
    public void setPricePerNight(double pricePerNight) {
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

    // method to set the hotel of the room
    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }

    public void addGuest(Guest guest) {
        guests.add(guest);
        // guest.getRooms().add(this);    // ensure bidirectional sync
    }

    public void removeGuest(Guest guest) {
        guests.remove(guest);
        guest.getRooms().remove(this);
    }

    public String toString() {
        return "#" + number + " " + type +
                (seaView ? " (sea)" : "") +
                " €" + pricePerNight +
                " @ " + (hotel != null ? hotel.getName() : "no-hotel" + "Description:" + description);
    }
}