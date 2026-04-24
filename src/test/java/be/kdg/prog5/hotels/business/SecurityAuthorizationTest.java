package be.kdg.prog5.hotels.business;

import be.kdg.prog5.hotels.data.SpringDataActivityLogRepository;
import be.kdg.prog5.hotels.data.SpringDataApplicationUserRepository;
import be.kdg.prog5.hotels.data.SpringDataGuestRepository;
import be.kdg.prog5.hotels.data.SpringDataHotelRepository;
import be.kdg.prog5.hotels.data.SpringDataRoomRepository;
import be.kdg.prog5.hotels.data.SpringDataStayRepository;
import be.kdg.prog5.hotels.domain.ApplicationUser;
import be.kdg.prog5.hotels.domain.Guest;
import be.kdg.prog5.hotels.domain.RoleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/* Integration Test Class
   PURPOSE: Verify that method-level Spring Security authorization works correctly for Guest deletion in the service layer */

@SpringBootTest
@ActiveProfiles("test")
class SecurityAuthorizationTest {

    @Autowired
    private GuestService guestService;

    @Autowired
    private SpringDataGuestRepository guestRepository;

    @Autowired
    private SpringDataApplicationUserRepository userRepository;

    @Autowired
    private SpringDataActivityLogRepository activityLogRepository;

    @Autowired
    private SpringDataStayRepository stayRepository;

    @Autowired
    private SpringDataRoomRepository roomRepository;

    @Autowired
    private SpringDataHotelRepository hotelRepository;

    @BeforeEach
    void setup() {
        // Clean database before each test so authorization checks are repeatable
        activityLogRepository.deleteAll();
        stayRepository.deleteAll();
        roomRepository.deleteAll();
        guestRepository.deleteAll();
        hotelRepository.deleteAll();
        userRepository.deleteAll();
    }

    /*
     PURPOSE: Verify that a normal user may delete their own Guest record
     SECURITY RULE: Owner is allowed
     */
    @Test
    @WithMockUser(username = "owner@test.com", roles = "USER")
    void ownerShouldDeleteOwnGuest() {

        // Arrange
        Guest guest = createGuestOwnedBy("owner@test.com", RoleType.USER);
        // Act
        guestService.deleteGuest(guest.getId());
        // Assert
        assertThat(guestRepository.findById(guest.getId())).isEmpty();
    }

    /*
     PURPOSE: Verify that a normal user may NOT delete another user's Guest record
     SECURITY RULE: Non-owner USER is blocked
     */
    @Test
    @WithMockUser(username = "other@test.com", roles = "USER")
    void otherUserShouldNotDeleteGuest() {

        // Arrange
        Guest guest = createGuestOwnedBy("owner@test.com", RoleType.USER);
        createUser("other@test.com", RoleType.USER);
        // Act + Assert
        assertThatThrownBy(() -> guestService.deleteGuest(guest.getId()))
                .isInstanceOf(AccessDeniedException.class);
        // Guest should still exist in the database
        assertThat(guestRepository.findById(guest.getId())).isPresent();
    }

    /*
     PURPOSE: Verify that an admin may delete any Guest record
     SECURITY RULE: ADMIN is allowed even when they are not the owner
     */
    @Test
    @WithMockUser(username = "admin@test.com", roles = "ADMIN")
    void adminShouldDeleteAnyGuest() {

        // Arrange
        createUser("admin@test.com", RoleType.ADMIN);
        Guest guest = createGuestOwnedBy("owner@test.com", RoleType.USER);
        // Act
        guestService.deleteGuest(guest.getId());
        // Assert
        assertThat(guestRepository.findById(guest.getId())).isEmpty();
    }

    /*
     PURPOSE: Verify that unauthenticated users cannot delete Guest records
     SECURITY RULE: Anonymous access is blocked
     */
    @Test
    @WithAnonymousUser
    void anonymousUserShouldNotDeleteGuest() {

        // Arrange
        Guest guest = createGuestOwnedBy("owner@test.com", RoleType.USER);
        // Act + Assert
        assertThatThrownBy(() -> guestService.deleteGuest(guest.getId()))
                .isInstanceOf(AccessDeniedException.class);
        // Guest should still exist in the database
        assertThat(guestRepository.findById(guest.getId())).isPresent();
    }

    /* Test Helper Method
       PURPOSE: Create a Guest linked to an owner user. Used to keep test methods shorter and cleaner
    */
    private Guest createGuestOwnedBy(String ownerEmail, RoleType role) {
        ApplicationUser owner = createUser(ownerEmail, role);

        Guest guest = new Guest(
                "Test Guest",
                LocalDate.of(1995, 5, 10),
                ownerEmail.replace("@", ".guest@"),
                "avatar.jpg"
        );
        guest.setOwner(owner);

        return guestRepository.saveAndFlush(guest);
    }

    /* Test Helper Method
       PURPOSE: Create and save a system user for authorization scenarios */
    private ApplicationUser createUser(String email, RoleType role) {
        return userRepository.saveAndFlush(
                new ApplicationUser(email, "password", role)
        );
    }
}