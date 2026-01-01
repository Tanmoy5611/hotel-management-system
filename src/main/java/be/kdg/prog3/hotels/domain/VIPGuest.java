package be.kdg.prog3.hotels.domain;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.time.LocalDate;

// JPA entity that extends Guest using SINGLE_TABLE inheritance
@Entity
@DiscriminatorValue("VIP")    // stored in the guest_type column ➡ JPA knows this row represents a VIPGuest
public class VIPGuest extends Guest {

    private double discountPercentage;

    // Required by Hibernate / JPA
    protected VIPGuest() {
    }

    // Constructor with super
    public VIPGuest(String fullName,
                    LocalDate dob,
                    String email,
                    boolean vip,
                    String avatarUrl,
                    double discountPercentage) {

        super(fullName, dob, email, vip, avatarUrl);  // used to initialize inherited fields from Guest entity
        this.discountPercentage = discountPercentage;
    }

    // Method overriding allows polymorphic behavior based on the actual entity type
    @Override
    public double getDiscountPercentage() {
        return this.discountPercentage;
    }

    public void setDiscountPercentage(double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    @Override
    public String toString() {
        return "VIPGuest{" +
                "discountPercentage=" + discountPercentage +
                "} " + super.toString();
    }
}