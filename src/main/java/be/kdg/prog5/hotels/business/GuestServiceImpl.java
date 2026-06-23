package be.kdg.prog5.hotels.business;

import be.kdg.prog5.hotels.business.exceptions.GuestAlreadyExistsException;
import be.kdg.prog5.hotels.business.exceptions.GuestNotFoundException;
import be.kdg.prog5.hotels.business.exceptions.BookingException;
import be.kdg.prog5.hotels.business.exceptions.RoomNotFoundException;
import be.kdg.prog5.hotels.config.AppConstants;
import be.kdg.prog5.hotels.data.SpringDataApplicationUserRepository;
import be.kdg.prog5.hotels.data.SpringDataGuestRepository;
import be.kdg.prog5.hotels.data.SpringDataRoomRepository;
import be.kdg.prog5.hotels.data.SpringDataStayRepository;
import be.kdg.prog5.hotels.domain.*;
import be.kdg.prog5.hotels.web.security.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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

    private static final String DEFAULT_GUEST_AVATAR_URL = "/images/guests/guest.jpg";

    private final SpringDataGuestRepository guestRepo;
    private final SpringDataRoomRepository roomRepo;
    private final SpringDataStayRepository stayRepo;
    private final SpringDataApplicationUserRepository userRepo;

    private final SecurityService securityService;
    private final SafeActivityLogger safeActivityLogger;

    // Injects repositories and services needed to manage guests and their bookings
    public GuestServiceImpl(SpringDataGuestRepository guestRepo,
                            SpringDataRoomRepository roomRepo,
                            SpringDataStayRepository stayRepo,
                            SpringDataApplicationUserRepository userRepo,
                            SecurityService securityService,
                            SafeActivityLogger safeActivityLogger) {
        this.guestRepo = guestRepo;
        this.roomRepo = roomRepo;
        this.stayRepo = stayRepo;
        this.userRepo = userRepo;
        this.securityService = securityService;
        this.safeActivityLogger = safeActivityLogger;
    }

    // Reads all guests for the guest overview page
    @Override
    @Transactional(readOnly = true)
    public List<Guest> getAllGuests() {
        log.debug("Getting all guests");

        return guestRepo.findAllWithOwner();
    }

    // Deletes a guest and clears cached search results because the list changed
    @Override
    // Removing a guest also frees any rooms from that guest's stays
    @CacheEvict(value = {"guestSearch", "roomSearch"}, allEntries = true)
    public void deleteGuest(Long guestId) {
        log.debug("Deleting guest with id {}", guestId);

        Guest guest = guestRepo.findById(guestId)
                .orElseThrow(() -> new GuestNotFoundException(guestId));

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

    // Reads all VIP guests from the inheritance query
    @Override
    @Transactional(readOnly = true)
    public List<Guest> getVipGuests() {
        log.debug("Getting all VIP guests");

        return guestRepo.findVipGuests();
    }

    // Caches guest search results by normalized search text and minimum room count
    // Repeating the same search can return from cache instead of querying the database again
    @Override
    @Transactional(readOnly = true)
    // Cache key uses normalized query text and minimum room count so equivalent searches reuse the same result
    @Cacheable(value = "guestSearch", key = "{#query == null ? '' : #query.trim().toLowerCase(), #minRooms == null || #minRooms < 1 ? null : #minRooms}")
    public List<Guest> searchGuests(String query, Integer minRooms) {
        log.debug("Searching guests: query={}, minRooms={}", query, minRooms);

        // Input sanitization belongs in the service - controller passes raw values
        String cleanedQuery = (query == null) ? "" : query.trim();

        Integer cleanedMinRooms = (minRooms == null || minRooms < 1)
                ? null
                : minRooms;

        return guestRepo.searchGuests(cleanedQuery, cleanedMinRooms);
    }

    // Creating a guest changes the guest list and may change stay counts
    // Clear all cached guest searches so the next search reads fresh data
    @Override
    // A guest created with a room changes both guest and availability searches
    @CacheEvict(value = {"guestSearch", "roomSearch"}, allEntries = true)
    public Guest createGuestWithRoom(String fullName, LocalDate dob, String email, String avatarUrl,
                                     BigDecimal discountPercentage, Long roomId,
                                     LocalDate checkIn, LocalDate checkOut) {
        log.debug("Creating guest {} with room {}", fullName, roomId);

        validateUniqueEmail(email);
        validateBookingDates(roomId, checkIn, checkOut);
        String cleanedAvatarUrl = normalizeAvatarUrl(avatarUrl);

        // Domain decision: VIP or regular Guest - belongs in the service
        Guest guest;
        if (discountPercentage != null && discountPercentage.compareTo(BigDecimal.ZERO) > 0) {
            guest = new VIPGuest(fullName, dob, email, cleanedAvatarUrl, discountPercentage);
        } else {
            guest = new Guest(fullName, dob, email, cleanedAvatarUrl);
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

            // Lock the room before assigning the new guest so concurrent bookings cannot overlap
            Room room = findRoomWithBookingLock(roomId);

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

    // Creating a guest from the Week 10 client also changes guest search results
    // Clear the guest search cache so the new guest appears immediately
    @Override
    @CacheEvict(value = "guestSearch", allEntries = true)
    public Guest createGuestFromClient(String fullName, LocalDate dob, String email, String avatarUrl,
                                       BigDecimal discountPercentage) {
        log.debug("Creating guest {} from Week 10 client", fullName);

        validateUniqueEmail(email);
        String cleanedAvatarUrl = normalizeAvatarUrl(avatarUrl);

        Guest guest;
        if (discountPercentage != null && discountPercentage.compareTo(BigDecimal.ZERO) > 0) {
            guest = new VIPGuest(fullName, dob, email, cleanedAvatarUrl, discountPercentage);
        } else {
            guest = new Guest(fullName, dob, email, cleanedAvatarUrl);
        }

        // Guests require an owner. Public client-created guests are assigned to the protected admin account
        ApplicationUser owner = userRepo.findByEmail(AppConstants.PROTECTED_ADMIN_EMAIL)
                .orElseThrow(() -> new IllegalStateException(
                        "Protected admin account is required before creating guests from the client"));
        guest.setOwner(owner);

        Guest savedGuest = guestRepo.save(guest);

        // The Week 10 client endpoint is public, so there is no logged-in user
        // Log the action under the protected admin owner that was assigned above
        safeActivityLogger.logAs(
                ActivityType.CREATE_GUEST,
                "Guest " + savedGuest.getFullName() + " created from Week 10 client",
                owner
        );

        return savedGuest;
    }

    // Loads one guest with stays, rooms, and hotels for the detail page
    @Override
    @Transactional(readOnly = true)
    public Guest getGuestWithDetails(Long guestId) {
        // Calling the JOIN FETCH method ensures stays are loaded before the session closes
        return guestRepo.findByIdWithDetails(guestId)
                .orElseThrow(() -> new GuestNotFoundException(guestId));
    }

    // Loads guest details for the guest overview page
    @Override
    @Transactional(readOnly = true)
    public GuestDetails getGuestDetails(Long guestId) {
        // Load stays once so the controller does not prepare domain data
        Guest guest = getGuestWithDetails(guestId);
        List<GuestDetails.StayDetails> stays = guest.getStays().stream()
                .map(stay -> new GuestDetails.StayDetails(
                        stay.getRoom(),
                        stay.getCheckInDate(),
                        stay.getCheckOutDate(),
                        stay.getNumberOfNights(),
                        stay.getGuest().getDiscountPercentage(),
                        stay.getTotalPrice(),
                        stay.getFinalPrice()))
                .toList();
        return new GuestDetails(guest, stays);
    }

    // validate unique email
    private void validateUniqueEmail(String email) {
        if (email != null && guestRepo.existsByEmailIgnoreCase(email.trim())) {
            throw new GuestAlreadyExistsException(email.trim());
        }
    }

    // validate booking dates
    private void validateBookingDates(Long roomId, LocalDate checkIn, LocalDate checkOut) {
        // A guest without a selected room does not need booking dates
        if (roomId == null) {
            return;
        }

        if (checkIn == null || checkOut == null) {
            throw new BookingException("booking.dates.required");
        }

        if (!checkOut.isAfter(checkIn)) {
            throw new BookingException("booking.checkout.after.checkin");
        }
    }

    // normalize avatar url
    private String normalizeAvatarUrl(String avatarUrl) {
        if (avatarUrl == null || avatarUrl.isBlank()) {
            return DEFAULT_GUEST_AVATAR_URL;
        }

        return avatarUrl.trim();
    }

    // First query obtains the database write lock on the room row
    // Second query reloads the full aggregate needed by Room.addGuest and logging
    private Room findRoomWithBookingLock(Long roomId) {
        roomRepo.findByIdForUpdate(roomId)
                .orElseThrow(() -> new RoomNotFoundException(roomId));

        return roomRepo.findByIdWithHotelAndGuests(roomId)
                .orElseThrow(() -> new RoomNotFoundException(roomId));
    }
}
