package be.kdg.prog5.hotels.webapi.dto;

import java.math.BigDecimal;

// Response data returned by the Room API without exposing the entity
public class RoomDto {

    private Long id;
    private int number;
    private String type;
    private BigDecimal pricePerNight;
    private boolean seaView;
    private String photoUrl;
    private String description;
    private String hotelId;
    private String hotelName;

    public RoomDto() {
    }

    public RoomDto(Long id, int number, String type, BigDecimal pricePerNight, boolean seaView,
                   String photoUrl, String description, String hotelId, String hotelName) {
        this.id = id;
        this.number = number;
        this.type = type;
        this.pricePerNight = pricePerNight;
        this.seaView = seaView;
        this.photoUrl = photoUrl;
        this.description = description;
        this.hotelId = hotelId;
        this.hotelName = hotelName;
    }

    // getters and setters
    public Long getId() {
        return id;
    }
    public int getNumber() {
        return number;
    }
    public String getType() {
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
    public String getHotelId() {
        return hotelId;
    }
    public String getHotelName() {
        return hotelName;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public void setNumber(int number) {
        this.number = number;
    }
    public void setType(String type) {
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
    public void setHotelId(String hotelId) {
        this.hotelId = hotelId;
    }
    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }
}