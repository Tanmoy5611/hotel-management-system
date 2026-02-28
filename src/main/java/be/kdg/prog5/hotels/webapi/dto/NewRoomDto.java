package be.kdg.prog5.hotels.webapi.dto;

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
    public String getHotelId() {
        return hotelId;
    }
    public void setHotelId(String hotelId) {
        this.hotelId = hotelId;
    }
}