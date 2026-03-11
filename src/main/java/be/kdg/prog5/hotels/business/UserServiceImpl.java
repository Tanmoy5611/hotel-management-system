package be.kdg.prog5.hotels.business;

import be.kdg.prog5.hotels.data.SpringDataUserRepository;
import be.kdg.prog5.hotels.domain.User;
import be.kdg.prog5.hotels.viewmodel.RegisterForm;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final SpringDataUserRepository userRepository;

    // encoder used to hash passwords before storing them
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(SpringDataUserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // return all users for the admin page
    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // create a new user with validation
    @Override
    public Optional<String> createUser(RegisterForm form) {

        // check if a user with the same email already exists
        if (userRepository.findByEmail(form.getEmail()).isPresent()) {
            return Optional.of("A user with this email already exists");
        }

        // create new user and encode the password
        User user = new User(
                form.getEmail(),
                passwordEncoder.encode(form.getPassword()),
                "USER"
        );

        // save user to the database
        userRepository.save(user);

        // return empty Optional if creation succeeded
        return Optional.empty();
    }

    // delete user but protect the main admin
    @Override
    public void deleteUser(Long id) {

        // load user or throw error if not found
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        /// prevent deletion of the main admin account
        if (user.getEmail().equals("admin@hotelapp.com")) {
            return;
        }

        userRepository.delete(user);
    }

    // toggle role between USER and ADMIN
    @Override
    public void toggleUserRole(Long id) {

        // load user
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // prevent changing role of the main admin
        if (user.getEmail().equals("admin@hotelapp.com")) {
            return;
        }

        // switch role
        if (user.getRole().equals("USER")) {
            user.setRole("ADMIN");
        } else {
            user.setRole("USER");
        }

        // save updated role
        userRepository.save(user);
    }
}