package be.kdg.prog5.hotels.business.customer;

import be.kdg.prog5.hotels.domain.Customer;
import be.kdg.prog5.hotels.domain.Stay;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

// Business methods for customer accounts and their own bookings
public interface CustomerService {
    // Registers a public customer account from the register page
    void register(String fullName, String email, LocalDate dob, String password);

    // Used by Spring Security to find customer login accounts
    Optional<Customer> findByEmail(String email);

    // Same lookup as findByEmail but throws when the customer is required
    Customer getCustomerByEmail(String email);

    // Loads bookings for the customer profile connected to this email
    List<Stay> getBookings(String email);

    // Builds the data needed by the logged in customer dashboard
    CustomerDashboard getDashboardForCurrentCustomer();

    // Books a room for the customer profile instead of letting the browser choose another guest
    void bookOwnRoom(String email, Long roomId, LocalDate checkIn, LocalDate checkOut);

    // Cancels a booking for the currently logged in customer
    void cancelOwnBookingForCurrentCustomer(Long stayId);

    // Cancels a booking only if it belongs to this customer
    void cancelOwnBooking(String email, Long stayId);

    // Admin action for enabling or disabling a customer login
    void toggleCustomerActive(Long id);
}