package be.kdg.prog5.hotels.config;

// Shared application-wide constants used across different layers
public final class AppConstants {

    // Main admin account (PROTECTED) seeded at startup and protected from delete/role changes
    public static final String PROTECTED_ADMIN_EMAIL = "admin@hotelapp.com";

    private AppConstants() {
    }
}