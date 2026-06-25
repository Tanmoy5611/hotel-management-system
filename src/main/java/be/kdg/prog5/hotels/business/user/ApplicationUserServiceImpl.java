package be.kdg.prog5.hotels.business.user;

import be.kdg.prog5.hotels.business.activity.SafeActivityLogger;
import be.kdg.prog5.hotels.business.exceptions.ApplicationUserNotFoundException;
import be.kdg.prog5.hotels.business.exceptions.ApplicationUserHasGuestsException;
import be.kdg.prog5.hotels.business.exceptions.ApplicationUserAlreadyExistsException;
import be.kdg.prog5.hotels.config.AppConstants;
import be.kdg.prog5.hotels.data.SpringDataApplicationUserRepository;
import be.kdg.prog5.hotels.data.SpringDataCustomerRepository;
import be.kdg.prog5.hotels.data.SpringDataGuestRepository;
import be.kdg.prog5.hotels.domain.ActivityType;
import be.kdg.prog5.hotels.domain.ApplicationUser;
import be.kdg.prog5.hotels.domain.Customer;
import be.kdg.prog5.hotels.domain.RoleType;
import be.kdg.prog5.hotels.viewmodel.AdminAccountRow;
import be.kdg.prog5.hotels.viewmodel.RegisterForm;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ApplicationUserServiceImpl implements ApplicationUserService {

    // protected admin account should not be deleted or role-changed
    private static final String PROTECTED_ADMIN_EMAIL = AppConstants.PROTECTED_ADMIN_EMAIL;

    private final SpringDataApplicationUserRepository userRepository;
    private final SpringDataGuestRepository guestRepository;
    private final SpringDataCustomerRepository customerRepository;

    private final SafeActivityLogger safeActivityLogger;

    // encoder used to hash passwords before storing them
    private final PasswordEncoder passwordEncoder;

    public ApplicationUserServiceImpl(SpringDataApplicationUserRepository userRepository,
                                      SpringDataGuestRepository guestRepository,
                                      SpringDataCustomerRepository customerRepository,
                                      SafeActivityLogger safeActivityLogger,
                                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.guestRepository = guestRepository;
        this.customerRepository = customerRepository;
        this.safeActivityLogger = safeActivityLogger;
        this.passwordEncoder = passwordEncoder;
    }

    // return all users for the admin page
    @Override
    @Transactional(readOnly = true)
    public List<ApplicationUser> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdminAccountRow> getAccountsForAdminPage() {
        // The admin table shows two account types in one simple view model
        List<AdminAccountRow> accounts = new ArrayList<>();

        // Application users are admin or staff and can have role actions
        for (ApplicationUser user : userRepository.findAll()) {
            accounts.add(new AdminAccountRow(
                    user.getId(),
                    user.getEmail(),
                    user.getRole().name(),
                    false,
                    true,
                    user.getEmail().equals(PROTECTED_ADMIN_EMAIL)
            ));
        }

        // Customers only get active or inactive actions in the admin table
        for (Customer customer : customerRepository.findAllWithProfiles()) {
            accounts.add(new AdminAccountRow(
                    customer.getId(),
                    customer.getProfile().getEmail(),
                    "CUSTOMER",
                    true,
                    customer.isActive(),
                    false
            ));
        }

        return accounts;
    }

    // return user by email
    @Override
    @Transactional(readOnly = true)
    public Optional<ApplicationUser> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // create a new user with validation
    @Override
    public void createUser(RegisterForm form) {

        // check if an applicationUser with the same email already exists
        if (userRepository.findByEmail(form.getEmail()).isPresent()) {
            throw new ApplicationUserAlreadyExistsException(form.getEmail());
        }

        // create new applicationUser and encode the password
        ApplicationUser applicationUser = new ApplicationUser(
                form.getEmail(),
                passwordEncoder.encode(form.getPassword()),
                RoleType.STAFF
        );

        // save applicationUser to the database
        userRepository.save(applicationUser);

        // Log activity for the user who created the account
        safeActivityLogger.log(
                ActivityType.CREATE_USER,
                "User " + applicationUser.getEmail() + " created"
        );

    }

    // delete user but protect the main admin
    @Override
    public void deleteUser(Long id) {

        // load applicationUser or throw error if not found
        ApplicationUser applicationUser = userRepository.findById(id)
                .orElseThrow(() -> new ApplicationUserNotFoundException(id));

        /// prevent deletion of the main admin account
        if (applicationUser.getEmail().equals(PROTECTED_ADMIN_EMAIL)) {
            return;
        }

        // Guests must always have an owner, so deleting a user with guests would violate the FK
        // Block the delete explicitly instead of letting the database fail with a vague error
        if (guestRepository.existsByOwner_Id(id)) {
            throw new ApplicationUserHasGuestsException(applicationUser.getEmail());
        }

        // capture email before delete (safe logging after entity removal)
        String email = applicationUser.getEmail();

        userRepository.delete(applicationUser);

        // Log activity for the user who deleted the account
        safeActivityLogger.log(ActivityType.DELETE_USER, "User " + email + " deleted");
    }

    // toggle role between STAFF and ADMIN
    @Override
    public void toggleUserRole(Long id) {

        // load applicationUser
        ApplicationUser applicationUser = userRepository.findById(id)
                .orElseThrow(() -> new ApplicationUserNotFoundException(id));

        // prevent changing the role of the main admin (Protected)
        if (applicationUser.getEmail().equals(PROTECTED_ADMIN_EMAIL)) {
            return;
        }

        // store old role before change
        RoleType oldRole = applicationUser.getRole();

        // switch role
        if (applicationUser.getRole() == RoleType.STAFF) {
            applicationUser.setRole(RoleType.ADMIN);
        } else {
            applicationUser.setRole(RoleType.STAFF);
        }

        // No save() needed -> JPA dirty checking handles the role update

        // Log activity for the user who changed the role
        safeActivityLogger.log(
                ActivityType.UPDATE_USER_ROLE,
                "User " + applicationUser.getEmail() +
                        " role changed from " + oldRole +
                        " to " + applicationUser.getRole()
        );
    }
}