package be.kdg.prog5.hotels.webapi.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class GuestDto {

    private Long id;
    private String fullName;
    private LocalDate dob;
    private String email;
    private String avatarUrl;
    private BigDecimal discountPercentage;
    private boolean vip;

    public GuestDto() {
    }

    public GuestDto(Long id, String fullName, LocalDate dob, String email, String avatarUrl,
                    BigDecimal discountPercentage, boolean vip) {
        this.id = id;
        this.fullName = fullName;
        this.dob = dob;
        this.email = email;
        this.avatarUrl = avatarUrl;
        this.discountPercentage = discountPercentage;
        this.vip = vip;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public BigDecimal getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(BigDecimal discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public boolean isVip() {
        return vip;
    }

    public void setVip(boolean vip) {
        this.vip = vip;
    }
}
