package be.kdg.prog5.hotels.webapi.dto;

import java.math.BigDecimal;

public class RoomDto {

    private Long id;
    private int number;
    private BigDecimal pricePerNight;
    private String hotelName;

    public RoomDto() {
    }

    public RoomDto(Long id, int number, BigDecimal pricePerNight, String hotelName) {
        this.id = id;
        this.number = number;
        this.pricePerNight = pricePerNight;
        this.hotelName = hotelName;
    }

    // getters and setters
    public Long getId() {
        return id;
    }
    public int getNumber() {
        return number;
    }
    public BigDecimal getPricePerNight() {
        return pricePerNight;
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
    public void setPricePerNight(BigDecimal pricePerNight) {
        this.pricePerNight = pricePerNight;
    }
    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }
}