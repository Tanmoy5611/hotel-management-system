package be.kdg.prog5.hotels.viewmodel;
import be.kdg.prog5.hotels.domain.RoomType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

// Simple ViewModel for Room form (used in add-room.html) - only handles form data and validation
public class RoomForm {

    // (Jakarta Validation)
    @Min(value = 1, message = "{room.number.min}")
    private int number;

    @NotNull(message = "{room.type.required}")
    private RoomType type;

    @NotNull(message = "{room.price.required}")
    @DecimalMin("0.00")
    private BigDecimal pricePerNight;

    private boolean seaView;

    @NotBlank(message = "{room.photo.required}")
    private String photoUrl;

    @Size(max = 1000)
    private String description;

    @NotBlank
    private String hotelId;

    // Getters and setters
    public int getNumber() {
        return number;
    }
    public void setNumber(int number) {
        this.number = number;
    }

    public RoomType getType() {
        return type;
    }
    public void setType(RoomType type) {
        this.type = type;
    }

    public BigDecimal getPricePerNight() {
        return pricePerNight;
    }
    public void setPricePerNight(BigDecimal pricePerNight) {
        this.pricePerNight = pricePerNight;
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