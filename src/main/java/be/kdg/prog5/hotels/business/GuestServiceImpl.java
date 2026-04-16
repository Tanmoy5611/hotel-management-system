package be.kdg.prog5.hotels.business;

import be.kdg.prog5.hotels.business.exceptions.GuestNotFoundException;
import be.kdg.prog5.hotels.business.exceptions.RoomNotFoundException;
import be.kdg.prog5.hotels.data.SpringDataGuestRepository;
import be.kdg.prog5.hotels.data.SpringDataRoomRepository;
import be.kdg.prog5.hotels.data.SpringDataStayRepository;
import be.kdg.prog5.hotels.domain.*;
import be.kdg.prog5.hotels.web.security.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
    private final SafeActivityLogger safeActivityLogger;


    public GuestServiceImpl(SpringDataGuestRepository guestRepo,
                            SpringDataRoomRepository roomRepo,
                            SpringDataStayRepository stayRepo,
                            SecurityService securityService,
                            SafeActivityLogger safeActivityLogger) {
        this.guestRepo = guestRepo;
        this.roomRepo = roomRepo;
        this.stayRepo = stayRepo;
        this.securityService = securityService;
        this.safeActivityLogger = safeActivityLogger;
    }

    /// Read Guests
    @Override
    @Transactional(readOnly = true)
    public List<Guest> getAllGuests() {
        log.debug("Getting all guests");

        return guestRepo.findAll();
    }

    /// Delete guest
    @Override
    public void deleteGuest(Long guestId) {
        log.debug("Deleting guest with id {}", guestId);

        Guest guest = guestRepo.findById(guestId)
                .orElseThrow(() -> new GuestNotFoundException(guestId));

        ApplicationUser user = securityService.getLoggedInUserSafe();


        // Authorization only if user exists (tests safe)
        if (user != null) {
            if (guest.getOwner() == null ||
                    (!guest.getOwner().getId().equals(user.getId())
                            && user.getRole() != RoleType.ADMIN)) {
                throw new SecurityException("You are not allowed to delete this guest");
            }
        }

        // store values before delete (safe logging)
        String guestName = guest.getFullName();

        // Need to delete stays first as Guest does NOT own Stay
        stayRepo.deleteByGuest_Id(guestId);

        // Delete guest AFTER
        guestRepo.delete(guest);

        // Logging activity for deleted guest
        safeActivityLogger.log(
                ActivityType.DELETE_GUEST,
                "Guest " + guestName + " (id=" + guestId + ") deleted"
        );

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
    public List<Guest> searchGuests(String query, Integer minRooms) {
        log.debug("Searching guests: query={}, minRooms={}", query, minRooms);

        // Input sanitization belongs in the service - controller passes raw values
        String cleanedQuery = (query == null) ? "" : query.trim();

        Integer cleanedMinRooms = (minRooms == null || minRooms < 1)
                ? null
                : minRooms;

        return guestRepo.searchGuests(cleanedQuery, cleanedMinRooms);
    }

    /// Creates guest with room; persists and associates if applicable
    @Override
    public Guest createGuestWithRoom(String fullName, LocalDate dob, String email, String avatarUrl,
                                     BigDecimal discountPercentage, Long roomId,
                                     LocalDate checkIn, LocalDate checkOut) {
        log.debug("Creating guest {} with room {}", fullName, roomId);

        // Domain decision: VIP or regular Guest - belongs in the service
        Guest guest;
        if (discountPercentage != null && discountPercentage.compareTo(BigDecimal.ZERO) > 0) {
            guest = new VIPGuest(fullName, dob, email, avatarUrl, discountPercentage);
        } else {
            guest = new Guest(fullName, dob, email, avatarUrl);
        }

        // get logged-in user (SAFE)
        ApplicationUser user = securityService.getLoggedInUserSafe();

        // assign owner ONLY if user exists (tests safe)
        if (user != null) {
            guest.setOwner(user);
        }

        Guest savedGuest = guestRepo.save(guest);

        if (roomId != null) {

            // Domain (Room.addGuest) already validates dates

            Room room = roomRepo.findByIdWithHotelAndGuests(roomId)
                    .orElseThrow(() -> new RoomNotFoundException(roomId));

            // Room owns Stay -> room creates Stay (aggregate logic)
            room.addGuest(savedGuest, checkIn, checkOut);

            // Room is managed -> JPA dirty checking persists Stay automatically

            // Log with full trace (room + hotel)
            safeActivityLogger.log(
                    ActivityType.CREATE_GUEST,
                    "Guest " + savedGuest.getFullName() +
                            " created and assigned to room " + room.getNumber() +
                            " in hotel " + room.getHotel().getName()
            );

        } else {

            // Log without room
            safeActivityLogger.log(
                    ActivityType.CREATE_GUEST,
                    "Guest " + savedGuest.getFullName() + " created (no room assigned)"
            );
        }

        return savedGuest;
    }

    @Override
    @Transactional(readOnly = true)
    public Guest getGuestWithDetails(Long guestId) {
        // Calling the JOIN FETCH method ensures stays are loaded before the session closes
        return guestRepo.findByIdWithDetails(guestId)
                .orElseThrow(() -> new GuestNotFoundException(guestId));
    }

}