package be.kdg.prog3.hotels.domain;

import java.util.HashSet;
import java.util.Set;
import jakarta.persistence.*;


@Entity
@Table(name = "rooms")
// Attributes of Room class
public class Room {

    @Id
    @Column(name = "number")
    private int number;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private RoomType type;

    @Column(name = "price_per_night")
    private double pricePerNight;

    @Column(name = "sea_view")
    private boolean seaView;

    @Column(name = "photo_url")
    private String photoUrl;

    //  Many rooms → one hotel
    @ManyToOne
    @JoinColumn(name = "hotel_id")     // Must match schema.sql
    private Hotel hotel;
    /// many-to-one

    // Many-to-Many: rooms_guests
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "rooms_guests",
            joinColumns = @JoinColumn(name = "room_number"),
            inverseJoinColumns = @JoinColumn(name = "guest_id")
    )

    private Set<Guest> guests = new HashSet<>();

    //Required empty constructor
    protected Room() {
    }


    // Constructor
    public Room(int number, RoomType type, double pricePerNight, boolean seaView, String photoUrl) {
        this.number = number;
        this.type = type;
        this.pricePerNight = pricePerNight;
        this.seaView = seaView;
        this.photoUrl = photoUrl;

    }

    // getters to access attributes
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
    public Hotel getHotel() {
        return hotel;
    }
    public Set<Guest> getGuests() {
        return guests;
    }


    // Setters
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


    // method to set the hotel of the room
    public void setHotel(Hotel hotel) {
        this.hotel = hotel;

//        if (hotel != null && !hotel.getRooms().contains(this)) {
//            hotel.addRoom(this);
//        }
    }

    public void addGuest(Guest guest) {
        guests.add(guest);
        guest.getRooms().add(this);    // ensure bidirectional sync
    }

    // Override toString method to print
    @Override
    public String toString() {
        String hotelName = (hotel != null ? hotel.getName() : "no-hotel");
        return "#" + number + " " + type + " " + (seaView ? "(sea)" : "") +
                " €" + pricePerNight + " @ " + hotelName;

    }
}