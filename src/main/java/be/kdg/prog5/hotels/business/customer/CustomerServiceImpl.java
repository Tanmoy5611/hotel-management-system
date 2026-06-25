package be.kdg.prog5.hotels.business.customer;

import be.kdg.prog5.hotels.business.booking.BookingService;
import be.kdg.prog5.hotels.business.exceptions.BookingNotFoundException;
import be.kdg.prog5.hotels.data.SpringDataCustomerRepository;
import be.kdg.prog5.hotels.data.SpringDataGuestRepository;
import be.kdg.prog5.hotels.data.SpringDataStayRepository;
import be.kdg.prog5.hotels.domain.Customer;
import be.kdg.prog5.hotels.domain.Guest;
import be.kdg.prog5.hotels.domain.Stay;
import be.kdg.prog5.hotels.business.security.SecurityService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {
    // Customer accounts use a Guest profile for the personal data
    private final SpringDataCustomerRepository customerRepository;
    private final SpringDataGuestRepository guestRepository;
    private final SpringDataStayRepository stayRepository;
    private final BookingService bookingService;
    private final PasswordEncoder passwordEncoder;
    private final SecurityService securityService;

    public CustomerServiceImpl(SpringDataCustomerRepository customerRepository,
                               SpringDataGuestRepository guestRepository,
                               SpringDataStayRepository stayRepository,
                               BookingService bookingService,
                               PasswordEncoder passwordEncoder,
                               SecurityService securityService) {
        this.customerRepository = customerRepository;
        this.guestRepository = guestRepository;
        this.stayRepository = stayRepository;
        this.bookingService = bookingService;
        this.passwordEncoder = passwordEncoder;
        this.securityService = securityService;
    }

    // creates a new customer account
    @Override
    public void register(String fullName, String email, LocalDate dob, String password) {
        // Store emails in one format so login and duplicate checks are predictable
        String cleanedEmail = email.trim().toLowerCase();
        if (guestRepository.existsByEmailIgnoreCase(cleanedEmail)) {
            throw new IllegalArgumentException("An account already exists for this email address.");
        }

        // A customer profile is also a guest, so future bookings can point to the same person
        Guest profile = new Guest(fullName.trim(), dob, cleanedEmail, null);
        Guest savedProfile = guestRepository.save(profile);
        customerRepository.save(new Customer(passwordEncoder.encode(password), savedProfile));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Customer> findByEmail(String email) {
        // Repository access stays inside the service layer
        return customerRepository.findByProfileEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public Customer getCustomerByEmail(String email) {
        return findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Customer account not found."));
    }

    // Get all bookings for a customer
    @Override
    @Transactional(readOnly = true)
    public List<Stay> getBookings(String email) {
        // Stays are loaded through the profile because bookings belong to Guest
        Guest profile = getCustomerByEmail(email).getProfile();
        return stayRepository.findByGuestIdWithDetails(profile.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerDashboard getDashboardForCurrentCustomer() {
        // The controller does not need to know how to find the logged in customer
        Customer customer = getCustomerByEmail(securityService.getLoggedInUsername());
        return new CustomerDashboard(customer.getProfile(), getBookings(customer.getProfile().getEmail()));
    }

    @Override
    public void bookOwnRoom(String email, Long roomId, LocalDate checkIn, LocalDate checkOut) {
        // Customer booking always uses their own profile id
        bookingService.bookRoom(roomId, getCustomerByEmail(email).getProfile().getId(), checkIn, checkOut);
    }

    @Override
    public void cancelOwnBookingForCurrentCustomer(Long stayId) {
        cancelOwnBooking(securityService.getLoggedInUsername(), stayId);
    }

    @Override
    public void cancelOwnBooking(String email, Long stayId) {
        Stay stay = stayRepository.findByIdWithBookingDetails(stayId)
                .orElseThrow(() -> new BookingNotFoundException(stayId));
        Long profileId = getCustomerByEmail(email).getProfile().getId();
        // Customers may only cancel stays linked to their own Guest profile
        if (!stay.getGuest().getId().equals(profileId)) {
            throw new AccessDeniedException("You can only cancel your own bookings.");
        }
        bookingService.cancelBooking(stayId);
    }

    @Override
    public void toggleCustomerActive(Long id) {
        // Admin can disable customer login without changing customer bookings
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer account not found."));
        customer.setActive(!customer.isActive());
    }
}