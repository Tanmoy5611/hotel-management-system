package be.kdg.prog5.hotels.webapi.dto;

import jakarta.validation.constraints.NotBlank;

// Request data for changing only a hotel description
public class UpdateHotelDescriptionDto {

    @NotBlank
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}