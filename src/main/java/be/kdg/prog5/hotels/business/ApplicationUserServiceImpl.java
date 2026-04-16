package be.kdg.prog5.hotels.business;

import be.kdg.prog5.hotels.business.exceptions.ApplicationUserNotFoundException;
import be.kdg.prog5.hotels.config.AppConstants;
import be.kdg.prog5.hotels.data.SpringDataApplicationUserRepository;
import be.kdg.prog5.hotels.domain.ActivityType;
import be.kdg.prog5.hotels.domain.ApplicationUser;
import be.kdg.prog5.hotels.domain.RoleType;
import be.kdg.prog5.hotels.viewmodel.RegisterForm;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ApplicationUserServiceImpl implements ApplicationUserService {

    // protected admin account should not be deleted or role-changed
    private static final String PROTECTED_ADMIN_EMAIL = AppConstants.PROTECTED_ADMIN_EMAIL;

    private final SpringDataApplicationUserRepository userRepository;

    private final SafeActivityLogger safeActivityLogger;

    // encoder used to hash passwords before storing them
    private final PasswordEncoder passwordEncoder;

    public ApplicationUserServiceImpl(SpringDataApplicationUserRepository userRepository,
                                      SafeActivityLogger safeActivityLogger,
                                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.safeActivityLogger = safeActivityLogger;
        this.passwordEncoder = passwordEncoder;
    }

    // return all users for the admin page
    @Override
    @Transactional(readOnly = true)
    public List<ApplicationUser> getAllUsers() {
        return userRepository.findAll();
    }

    // create a new user with validation
    @Override
    public Optional<String> createUser(RegisterForm form) {

        // check if an applicationUser with the same email already exists
        if (userRepository.findByEmail(form.getEmail()).isPresent()) {
            return Optional.of("A applicationUser with this email already exists");
        }

        // create new applicationUser and encode the password
        ApplicationUser applicationUser = new ApplicationUser(
                form.getEmail(),
                passwordEncoder.encode(form.getPassword()),
                RoleType.USER
        );

        // save applicationUser to the database
        userRepository.save(applicationUser);

        // Log activity for the user who created the account
        safeActivityLogger.log(
                ActivityType.CREATE_USER,
                "User " + applicationUser.getEmail() + " created"
        );

        // return empty Optional if creation succeeded
        return Optional.empty();
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

        // capture email before delete (safe logging after entity removal)
        String email = applicationUser.getEmail();

        userRepository.delete(applicationUser);

        // Log activity for the user who deleted the account
        safeActivityLogger.log(ActivityType.DELETE_USER, "User " + email + " deleted");
    }

    // toggle role between USER and ADMIN
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
        if (applicationUser.getRole() == RoleType.USER) {
            applicationUser.setRole(RoleType.ADMIN);
        } else {
            applicationUser.setRole(RoleType.USER);
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