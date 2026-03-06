package be.kdg.prog5.hotels.webapi.dto;

import jakarta.validation.constraints.NotBlank;

public class UpdateRoomDescriptionDto {

    @NotBlank //  ensures 400
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}