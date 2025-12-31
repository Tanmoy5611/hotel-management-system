package be.kdg.prog3.hotels.domain;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.time.LocalDate;

@Entity
@DiscriminatorValue("VIP")
public class VIPGuest extends Guest {

    private double discountPercentage;

    protected VIPGuest() {
    }

    public VIPGuest(String fullName,
                    LocalDate dob,
                    String email,
                    boolean vip,
                    String avatarUrl,
                    double discountPercentage) {

        super(fullName, dob, email, vip, avatarUrl);
        this.discountPercentage = discountPercentage;
    }

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