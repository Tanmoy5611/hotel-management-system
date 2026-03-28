package be.kdg.prog5.hotels.business;

import be.kdg.prog5.hotels.data.SpringDataGuestRepository;
import be.kdg.prog5.hotels.data.SpringDataRoomRepository;
import be.kdg.prog5.hotels.data.SpringDataStayRepository;
import be.kdg.prog5.hotels.domain.*;
import be.kdg.prog5.hotels.web.security.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional

public class GuestServiceImpl implements GuestService {

    private static final Logger log =
            LoggerFactory.getLogger(GuestServiceImpl.class);


    private final SpringDataGuestRepository guestRepo;
    private final SpringDataRoomRepository roomRepo;
    private final SpringDataStayRepository stayRepo;

    private final SecurityService securityService;
    private final ActivityLogService activityLogService;


    public GuestServiceImpl(SpringDataGuestRepository guestRepo,
                            SpringDataRoomRepository roomRepo,
                            SpringDataStayRepository stayRepo,
                            SecurityService securityService,
                            ActivityLogService activityLogService) {
        this.guestRepo = guestRepo;
        this.roomRepo = roomRepo;
        this.stayRepo = stayRepo;
        this.securityService = securityService;
        this.activityLogService = activityLogService;
    }

    /// Read Guests
    @Override
    @Transactional(readOnly = true)
    public List<Guest> getAllGuests() {
        log.debug("Getting all guests");

        return guestRepo.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Guest> getGuestsByRoom(Long roomId) {
        log.debug("Getting guests for room {}", roomId);

        return guestRepo.findByRoom(roomId);
    }

    /// Delete guest
    @Override
    public void deleteGuest(Long guestId) {
        log.debug("Deleting guest with id {}", guestId);

        Guest guest = guestRepo.findById(guestId)
                .orElseThrow(() -> new IllegalArgumentException("Guest not found"));

        ApplicationUser user = securityService.getLoggedInUserSafe();


        // Authorization only if user exists (tests safe)
        if (user != null) {
            if (!guest.getOwner().getId().equals(user.getId())
                    && user.getRole() != RoleType.ADMIN) {
                throw new IllegalStateException("You are not allowed to delete this guest");
            }
        }

        // Need to delete stays first as Guest does NOT own Stay
        stayRepo.deleteByGuest_Id(guestId);

        // Delete guest AFTER
        guestRepo.delete(guest);

        // Logging activity for deleted guest
        if (user != null) {
            activityLogService.log(
                    ActivityType.DELETE_GUEST,
                    "Guest " + guest.getFullName() + " (id=" + guestId + ") deleted",
                    user
            );
        }

    }

    /// Search Guest
    @Override
    @Transactional(readOnly = true)
    public List<Guest> getVipGuests() {
        log.debug("Getting all VIP guests");

        return guestRepo.findVipGuests();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Guest> searchGuestsByName(String name) {
        log.debug("Searching guests with name containing {}", name);

        return guestRepo.findByFullNameContainingIgnoreCase(name);
    }

    @Override
    public List<Guest> getGuestsWithManyRooms(int minRooms) {
        log.debug("Getting guests with more than {} rooms", minRooms);

        return guestRepo.findGuestsWithMoreThanRooms(minRooms);
    }

    /// Creates guest with room; persists and associates if applicable
    @Override
    public Guest createGuestWithRoom(Guest guest, Long roomId, LocalDate checkIn, LocalDate checkOut) {
        log.debug("Creating guest {} with room {}", guest, roomId);

        // get logged-in user (SAFE)
        ApplicationUser user = securityService.getLoggedInUserSafe();

        // assign owner ONLY if user exists (tests safe)
        if (user != null) {
            guest.setOwner(user);
        }

        Guest savedGuest = guestRepo.save(guest);

        if (roomId != null) {

            // validate dates
            if (checkIn == null || checkOut == null) {
                throw new IllegalArgumentException("Check-in and check-out dates are required when assigning a room");
            }

            Room room = roomRepo.findById(roomId)
                    .orElseThrow(() -> new IllegalArgumentException("Room not found"));

            // Room owns Stay -> room creates Stay (aggregate logic)
            room.addGuest(savedGuest, checkIn, checkOut);

            // Cascade on Room will persist Stay automatically
            roomRepo.save(room);

            // Log with full trace (room + hotel)
            if (user != null) {
                activityLogService.log(
                        ActivityType.CREATE_GUEST,
                        "Guest " + savedGuest.getFullName() +
                                " created and assigned to room " + room.getNumber() +
                                " in hotel " + room.getHotel().getName(),
                        user
                );
            }

        } else {

            // Log without room
            if (user != null) {
                activityLogService.log(
                        ActivityType.CREATE_GUEST,
                        "Guest " + savedGuest.getFullName() + " created (no room assigned)",
                        user
                );
            }
        }

        return savedGuest;
    }

    @Override
    @Transactional(readOnly = true)
    public Guest getGuestWithDetails(Long guestId) {
        // Calling the JOIN FETCH method ensures stays are loaded before the session closes
        return guestRepo.findByIdWithDetails(guestId)
                .orElseThrow(() -> new IllegalArgumentException("Guest not found"));
    }
}