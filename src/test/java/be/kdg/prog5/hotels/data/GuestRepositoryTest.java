package be.kdg.prog5.hotels.data;

import be.kdg.prog5.hotels.domain.*;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class GuestRepositoryTest {

    @Autowired
    private SpringDataGuestRepository guestRepository;

    @Autowired
    private SpringDataApplicationUserRepository userRepository;

    @Autowired
    private SpringDataStayRepository stayRepository;

    @Autowired
    private SpringDataRoomRepository roomRepository;

    @Autowired
    private SpringDataHotelRepository hotelRepository;

    private ApplicationUser user;

    @BeforeEach
    void setup() {
        // Arrange (global)
        // Clean DB before each test -> ensures test isolation -> prevents interference between tests
        stayRepository.deleteAll();
        roomRepository.deleteAll();
        guestRepository.deleteAll();
        hotelRepository.deleteAll();
        userRepository.deleteAll();

        // create required ApplicationUser (owner is mandatory FK)
        // Guest.owner is NOT NULL -> must exist before creating Guest
        user = new ApplicationUser(
                "test@test.com",
                "password",
                RoleType.STAFF
        );

        // saveAndFlush ensures immediate DB sync (avoids delayed constraint failures later)
        userRepository.saveAndFlush(user);
    }

    // NOTE: no @Transactional as it hides real database behavior and can prevent detecting lazy loading issues


    /*
     PURPOSE: Verify that deleting a single Guest works correctly.
     EXPECTATION: After deleteById(), the Guest should no longer exist in the DB.
     IMPORTANT: Confirms basic repository delete behavior.
     */
    @Test
    void shouldDeleteGuestSuccessfully() {
        // Arrange
        // create valid guest entity
        Guest guest = new Guest(
                "John Doe",
                LocalDate.of(1995, 5, 10),
                "john@test.com",
                "avatar.jpg"
        );

        // owner is required (Not NULL FK)
        guest.setOwner(user);
        guestRepository.save(guest);

        Long guestId = guest.getId();

        // Act
        guestRepository.deleteById(guestId);
        guestRepository.flush();

        // Assert
        assertThat(guestRepository.findById(guestId)).isEmpty();
    }


    /*
     PURPOSE: Verify deleteAll() removes all Guest records.
     EXPECTATION: After deleteAll(), table should be empty.
     IMPORTANT: Confirms bulk delete operation works correctly.
     */
    @Test
    void shouldDeleteAllGuests() {
        // Arrange
        Guest guest1 = new Guest(
                "A",
                LocalDate.now(),
                "a@test.com",
                "avatar1.jpg"
        );

        Guest guest2 = new Guest(
                "B",
                LocalDate.now(),
                "b@test.com",
                "avatar2.jpg"
        );

        // both need owner (FK constraint)
        guest1.setOwner(user);
        guest2.setOwner(user);

        guestRepository.save(guest1);
        guestRepository.save(guest2);

        // Act
        guestRepository.deleteAll();

        // Assert
        // verify table is empty
        assertThat(guestRepository.count()).isEqualTo(0);
    }


    /*
     PURPOSE: Verify Bean Validation (@NotNull) on email field.
     EXPECTATION: Saving a Guest with null email should fail BEFORE hitting the DB.
     IMPORTANT: ConstraintViolationException comes from validation layer (Hibernate Validator), not from the db.
     WHY saveAndFlush(): Forces validation + SQL execution immediately.
     */
    @Test
    void shouldFailWhenEmailIsNull() {
        // Arrange
        Guest guest = new Guest(
                "No Email",
                LocalDate.now(),
                null,       // invalid - violates @NotNull / validation
                "avatar.jpg"
        );

        guest.setOwner(user);

        // Act + Assert
        // Bean Validation happens before DB - ConstraintViolationException (occurs during validation phase (before DB)
        // database constraints are only enforced when SQL is executed, which happens during flush
        assertThatThrownBy(() -> guestRepository.saveAndFlush(guest))
                .isInstanceOf(ConstraintViolationException.class);
    }

    /*
     PURPOSE: Verify UNIQUE constraint on email column.
     EXPECTATION: Two Guests cannot have the same email.
     IMPORTANT: This is enforced by the database - NOT by Hibernate directly.
     RESULT: Exception occurs during flush -> DataIntegrityViolationException.
     */
    @Test
    void shouldFailWhenDuplicateEmail() {
        // Arrange
        Guest g1 = new Guest(
                "A",
                LocalDate.now(),
                "dup@test.com",
                "a.jpg"
        );

        // guest with duplicate email
        Guest g2 = new Guest(
                "B",
                LocalDate.now(),
                "dup@test.com",
                "b.jpg"
        );

        g1.setOwner(user);
        g2.setOwner(user);

        //  first guest insert succeeds
        guestRepository.saveAndFlush(g1);

        // Act + Assert
        // Second insert violates DB UNIQUE constraint
        assertThatThrownBy(() -> guestRepository.saveAndFlush(g2))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void creatingGuestWithoutOwnerShouldSucceed() {

        // Arrange
        Guest guest = new Guest(
                "John Doe",
                LocalDate.of(1995, 5, 10),
                "john@test.com",
                "avatar.jpg"
        );

        // Customer profiles are also guests and do not need an ApplicationUser owner

        // Act
        Guest savedGuest = guestRepository.saveAndFlush(guest);

        // Assert
        assertThat(savedGuest.getId()).isNotNull();
        assertThat(savedGuest.getOwner()).isNull();
    }
}