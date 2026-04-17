package be.kdg.prog5.hotels.webapi.dto;

import be.kdg.prog5.hotels.domain.RoomType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public class NewRoomDto {

    @NotNull                  // triggers 400
    @Positive                 // triggers 400
    private Integer number;   // Using Integer instead of int (so validation works properly)

    @NotNull
    @Positive
    private BigDecimal pricePerNight;

    @NotNull
    private RoomType type;

    private boolean seaView;

    private String photoUrl;

    private String description;

    @NotNull
    private String hotelId;

    // getters & setters
    public Integer getNumber() {
        return number;
    }
    public void setNumber(Integer number) {
        this.number = number;
    }
    public BigDecimal getPricePerNight() {
        return pricePerNight;
    }
    public void setPricePerNight(BigDecimal pricePerNight) {
        this.pricePerNight = pricePerNight;
    }
    public RoomType getType() {
        return type;
    }
    public void setType(RoomType type) {
        this.type = type;
    }
    public boolean isSeaView() {
        return seaView;
    }
    public void setSeaView(boolean seaView) {
        this.seaView = seaView;
    }
    public String getPhotoUrl() {
        return photoUrl;
    }
    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getHotelId() {
        return hotelId;
    }
    public void setHotelId(String hotelId) {
        this.hotelId = hotelId;
    }
}