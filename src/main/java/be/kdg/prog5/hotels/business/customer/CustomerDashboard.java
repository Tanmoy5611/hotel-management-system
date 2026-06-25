package be.kdg.prog5.hotels.business.customer;

import be.kdg.prog5.hotels.domain.Guest;
import be.kdg.prog5.hotels.domain.Stay;

import java.util.List;

// Small object for the customer dashboard page
// Keeps the controller from preparing profile and booking data itself
public record CustomerDashboard(Guest profile, List<Stay> bookings) {
}