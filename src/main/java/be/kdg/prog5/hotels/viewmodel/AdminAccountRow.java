package be.kdg.prog5.hotels.viewmodel;

// One row for the admin user management table
// It can represent either an application user or a customer account
public class AdminAccountRow {
    private final Long id;
    private final String email;
    private final String role;
    // True means the row is a customer and should not show role change actions
    private final boolean customer;
    // Used only for customer rows because staff and admin accounts are always active
    private final boolean active;
    // Keeps the main admin account protected in the table
    private final boolean protectedAdmin;

    public AdminAccountRow(Long id, String email, String role, boolean customer, boolean active, boolean protectedAdmin) {
        this.id = id;
        this.email = email;
        this.role = role;
        this.customer = customer;
        this.active = active;
        this.protectedAdmin = protectedAdmin;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public boolean isCustomer() {
        return customer;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isProtectedAdmin() {
        return protectedAdmin;
    }
}