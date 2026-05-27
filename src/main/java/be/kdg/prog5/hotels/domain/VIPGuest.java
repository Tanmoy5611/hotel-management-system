package be.kdg.prog5.hotels.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.math.BigDecimal;
import java.time.LocalDate;

// JPA entity that extends Guest using SINGLE_TABLE inheritance
@Entity
@DiscriminatorValue("VIP")    // stored in the guest_type column ➡ JPA knows this row represents a VIPGuest
public class VIPGuest extends Guest {

    // Required by Hibernate / JPA
    protected VIPGuest() {
    }

    // Constructor with super
    public VIPGuest(String fullName, LocalDate dob, String email,
                    String avatarUrl, BigDecimal discountPercentage) {

        super(fullName, dob, email, avatarUrl);  // used to initialize inherited fields from Guest entity

        // Delegate to the setter so constructor and later updates use one validation rule
        setDiscountPercentage(discountPercentage);
    }

    // set discount percentage to be between 0 and 100 for VIP guests
    @Override
    public void setDiscountPercentage(BigDecimal discountPercentage) {

        if (discountPercentage != null &&
                (discountPercentage.compareTo(BigDecimal.ZERO) < 0 ||
                        discountPercentage.compareTo(BigDecimal.valueOf(100)) > 0)) {

            throw new IllegalArgumentException("Discount must be between 0 and 100");
        }

        super.setDiscountPercentage(discountPercentage);
    }

    @Override
    public String toString() {
        return "VIPGuest{" +
                "discountPercentage=" + getDiscountPercentage() +
                "} " + super.toString();
    }
}