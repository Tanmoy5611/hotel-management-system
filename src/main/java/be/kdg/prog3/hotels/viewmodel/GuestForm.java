package be.kdg.prog3.hotels.viewmodel;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public class GuestForm {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotNull(message = "Date of birth required")
    private LocalDate dob;

    @Email(message = "Invalid email")
    @NotBlank(message = "Email is required")
    private String email;

    private boolean vip;

    // Only used if VIP
    @Min(0) @Max(100)
    private double discountPercentage;

    private String avatarUrl;

    // NEW: assign room during creation (optional)
    private Integer roomNumber;

    // getters + setters
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public boolean isVip() { return vip; }
    public void setVip(boolean vip) { this.vip = vip; }

    public double getDiscountPercentage() { return discountPercentage; }
    public void setDiscountPercentage(double discountPercentage) { this.discountPercentage = discountPercentage; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public Integer getRoomNumber() { return roomNumber; }
    public void setRoomNumber(Integer roomNumber) { this.roomNumber = roomNumber; }
}