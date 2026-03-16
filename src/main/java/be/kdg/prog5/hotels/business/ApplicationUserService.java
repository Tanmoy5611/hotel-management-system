package be.kdg.prog5.hotels.business;

import be.kdg.prog5.hotels.domain.ApplicationUser;
import be.kdg.prog5.hotels.viewmodel.RegisterForm;

import java.util.List;
import java.util.Optional;

public interface ApplicationUserService {

    // get all users for admin panel
    List<ApplicationUser> getAllUsers();

    // create a new user from the register form
    Optional<String> createUser(RegisterForm form);

    // delete a user by id
    void deleteUser(Long id);

    // switch role USER <-> ADMIN
    void toggleUserRole(Long id);
}