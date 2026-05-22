package be.kdg.prog5.hotels.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

// Attributes of Guest class
@Entity                         // Marks this class as a JPA entity - it will be stored in a database table
@Table(name = "guests")         // Explicitly maps this entity to the "guests" table in the database.
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)   // One table for Guest + VIPGuest
@DiscriminatorColumn(name = "guest_type")               // "guest_type" column that decides subtype
@DiscriminatorValue("GUEST")                            // Value for normal Guest
public class Guest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // primary key which is auto created by database
    private Long id;

    @NotBlank
    @Column(name = "full_name", nullable = false)
    private String fullName;

    @ManyToOne(optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private ApplicationUser owner;

    private LocalDate dob;

    @NotBlank
    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "avatar_url")                         // Maps Java field avatarUrl to database column avatar_url
    private String avatarUrl;

    @Column(name = "discount_percentage", nullable = false)
    private BigDecimal discountPercentage = BigDecimal.ZERO;

    // Guest is an INDEPENDENT aggregate & it participates in Stay but does NOT own Stay lifecycle
    // Therefore: NO cascade, NO orphanRemoval
    @OneToMany(mappedBy = "guest", fetch = FetchType.LAZY)
    private List<Stay> stays = new ArrayList<>();

    // Empty constructor
    protected Guest() {
    }

    // Constructor
    public Guest(String fullName, LocalDate dob, String email, String avatarUrl) {
        this.fullName = fullName;
        this.dob = dob;
        this.email = email;
        this.avatarUrl = avatarUrl;
    }

    // getters
    public Long getId() {
        return id;
    }
    public String getFullName() {
        return fullName;
    }
    public ApplicationUser getOwner() {
        return owner;
    }
    public LocalDate getDob() {
        return dob;
    }
    public String getEmail() {
        return email;
    }
    public String getAvatarUrl() {
        return avatarUrl;
    }

    //  Return unmodifiable list to prevent direct collection manipulation
    public List<Stay> getStays() {
        return Collections.unmodifiableList(stays);
    }

    // Setters
    /// public void setId(Long id) {this.id = id;}       // No set id (primary key) method bc it is auto generated
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    public void setOwner(ApplicationUser owner) {
        this.owner = owner;
    }
    public void setDob(LocalDate dob) {
        this.dob = dob;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    // polymorphism method
    public BigDecimal getDiscountPercentage() {
        return discountPercentage;
    }

    protected void setDiscountPercentage(BigDecimal discountPercentage) {
        this.discountPercentage = discountPercentage == null
                ? BigDecimal.ZERO
                : discountPercentage;
    }

    // Override to string method for guests
    @Override
    public String toString() {
        return String.format("%s (dob=%s, email=%s)", fullName, dob, email);
    }
}