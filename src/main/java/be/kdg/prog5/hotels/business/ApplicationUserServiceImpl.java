package be.kdg.prog5.hotels.business;

import be.kdg.prog5.hotels.data.SpringDataApplicationUserRepository;
import be.kdg.prog5.hotels.domain.ApplicationUser;
import be.kdg.prog5.hotels.viewmodel.RegisterForm;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ApplicationUserServiceImpl implements ApplicationUserService {

    private final SpringDataApplicationUserRepository userRepository;

    // encoder used to hash passwords before storing them
    private final PasswordEncoder passwordEncoder;

    public ApplicationUserServiceImpl(SpringDataApplicationUserRepository userRepository,
                                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
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

        // check if a applicationUser with the same email already exists
        if (userRepository.findByEmail(form.getEmail()).isPresent()) {
            return Optional.of("A applicationUser with this email already exists");
        }

        // create new applicationUser and encode the password
        ApplicationUser applicationUser = new ApplicationUser(
                form.getEmail(),
                passwordEncoder.encode(form.getPassword()),
                "USER"
        );

        // save applicationUser to the database
        userRepository.save(applicationUser);

        // return empty Optional if creation succeeded
        return Optional.empty();
    }

    // delete user but protect the main admin
    @Override
    public void deleteUser(Long id) {

        // load applicationUser or throw error if not found
        ApplicationUser applicationUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ApplicationUser not found"));

        /// prevent deletion of the main admin account
        if (applicationUser.getEmail().equals("admin@hotelapp.com")) {
            return;
        }

        userRepository.delete(applicationUser);
    }

    // toggle role between USER and ADMIN
    @Override
    public void toggleUserRole(Long id) {

        // load applicationUser
        ApplicationUser applicationUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ApplicationUser not found"));

        // prevent changing the role of the main admin (Protected)
        if (applicationUser.getEmail().equals("admin@hotelapp.com")) {
            return;
        }

        // switch role
        if (applicationUser.getRole().equals("USER")) {
            applicationUser.setRole("ADMIN");
        } else {
            applicationUser.setRole("USER");
        }

        // save updated role
        userRepository.save(applicationUser);
    }
}