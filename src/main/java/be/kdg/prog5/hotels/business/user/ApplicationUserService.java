package be.kdg.prog5.hotels.business.user;

import be.kdg.prog5.hotels.domain.ApplicationUser;
import be.kdg.prog5.hotels.viewmodel.AdminAccountRow;
import be.kdg.prog5.hotels.viewmodel.RegisterForm;

import java.util.List;
import java.util.Optional;

public interface ApplicationUserService {

    // get all users for admin panel
    List<ApplicationUser> getAllUsers();

    // Combines staff, admin, and customer accounts for the admin table
    List<AdminAccountRow> getAccountsForAdminPage();

    // Used by Spring Security while a user logs in
    Optional<ApplicationUser> findByEmail(String email);

    // create a new user from the register form
    void createUser(RegisterForm form);

    // delete a user by id
    void deleteUser(Long id);

    // switch role STAFF <-> ADMIN
    void toggleUserRole(Long id);
}