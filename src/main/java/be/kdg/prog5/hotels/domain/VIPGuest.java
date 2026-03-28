package be.kdg.prog5.hotels.domain;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.math.BigDecimal;
import java.time.LocalDate;

// JPA entity that extends Guest using SINGLE_TABLE inheritance
@Entity
@DiscriminatorValue("VIP")    // stored in the guest_type column ➡ JPA knows this row represents a VIPGuest
public class VIPGuest extends Guest {

    @Column(name = "discount_percentage", nullable = true)
    private BigDecimal discountPercentage = BigDecimal.ZERO;  //  no null problems

    // Required by Hibernate / JPA
    protected VIPGuest() {
    }

    // Constructor with super
    public VIPGuest(String fullName, LocalDate dob, String email,
                    String avatarUrl, BigDecimal discountPercentage) {

        super(fullName, dob, email, avatarUrl);  // used to initialize inherited fields from Guest entity
        this.discountPercentage = discountPercentage;
    }

    // Method overriding allows polymorphic behavior based on the actual entity type
    @Override
    public BigDecimal getDiscountPercentage() {
        return this.discountPercentage;
    }

    public void setDiscountPercentage(BigDecimal discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    @Override
    public String toString() {
        return "VIPGuest{" +
                "discountPercentage=" + discountPercentage +
                "} " + super.toString();
    }
}