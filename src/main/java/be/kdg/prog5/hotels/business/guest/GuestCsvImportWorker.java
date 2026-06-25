package be.kdg.prog5.hotels.business.guest;

import be.kdg.prog5.hotels.business.activity.SafeActivityLogger;
import be.kdg.prog5.hotels.data.SpringDataApplicationUserRepository;
import be.kdg.prog5.hotels.data.SpringDataGuestRepository;
import be.kdg.prog5.hotels.domain.ActivityType;
import be.kdg.prog5.hotels.domain.ApplicationUser;
import be.kdg.prog5.hotels.domain.Guest;
import be.kdg.prog5.hotels.domain.VIPGuest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Service
public class GuestCsvImportWorker {

    private static final Logger log = LoggerFactory.getLogger(GuestCsvImportWorker.class);
    private static final String DEFAULT_GUEST_AVATAR_URL = "/images/guests/guest.jpg";

    private final SpringDataGuestRepository guestRepo;
    private final SpringDataApplicationUserRepository userRepo;
    private final SafeActivityLogger safeActivityLogger;

    // Injects repositories and logger needed by the asynchronous CSV import
    public GuestCsvImportWorker(SpringDataGuestRepository guestRepo,
                                SpringDataApplicationUserRepository userRepo,
                                SafeActivityLogger safeActivityLogger) {
        this.guestRepo = guestRepo;
        this.userRepo = userRepo;
        this.safeActivityLogger = safeActivityLogger;
    }

    @Async
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "guestSearch", allEntries = true, beforeInvocation = true),
            @CacheEvict(value = "guestSearch", allEntries = true)
    })
    // Runs CSV processing on an async thread so the upload request can return immediately
    public void importGuests(byte[] csvBytes, String ownerEmail) {
        log.info("Starting async guest CSV import on thread {}", Thread.currentThread().getName());

        ApplicationUser owner = userRepo.findByEmail(ownerEmail)
                .orElseThrow(() -> new IllegalArgumentException("CSV import owner not found: " + ownerEmail));

        // Counters are used for the final activity log
        int created = 0;
        int skipped = 0;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new ByteArrayInputStream(csvBytes), StandardCharsets.UTF_8))) {

            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;

                // Ignore empty lines and the common header row
                if (line.isBlank() || lineNumber == 1 && line.toLowerCase().startsWith("fullname,")) {
                    continue;
                }

                try {
                    Guest guest = parseGuest(line);

                    if (guestRepo.existsByEmailIgnoreCase(guest.getEmail())) {
                        skipped++;
                        log.warn("Skipping duplicate guest email on CSV line {}: {}", lineNumber, guest.getEmail());
                        continue;
                    }

                    // Imported guests belong to the admin who started the upload
                    guest.setOwner(owner);
                    guestRepo.save(guest);
                    created++;

                } catch (IllegalArgumentException ex) {
                    skipped++;
                    log.warn("Skipping invalid guest CSV line {}: {}", lineNumber, ex.getMessage());
                }
            }

            safeActivityLogger.logAs(
                    ActivityType.CREATE_GUEST,
                    "CSV guest import finished: " + created + " created, " + skipped + " skipped",
                    owner
            );

            log.info("Finished async guest CSV import: {} created, {} skipped", created, skipped);

        } catch (IOException ex) {
            throw new IllegalStateException("Could not read uploaded guest CSV", ex);
        }
    }

    // Converts one CSV line into either a regular Guest or VIPGuest
    private Guest parseGuest(String line) {
        String[] columns = line.split(",", -1);

        if (columns.length != 5) {
            throw new IllegalArgumentException("expected 5 columns but got " + columns.length);
        }

        String fullName = columns[0].trim();
        String email = columns[1].trim();
        LocalDate dob = parseDate(columns[2].trim());
        String avatarUrl = normalizeAvatarUrl(columns[3]);
        BigDecimal discountPercentage = parseDiscount(columns[4].trim());

        if (fullName.isBlank()) {
            throw new IllegalArgumentException("fullName is required");
        }

        if (email.isBlank()) {
            throw new IllegalArgumentException("email is required");
        }

        if (discountPercentage.compareTo(BigDecimal.ZERO) > 0) {
            return new VIPGuest(fullName, dob, email, avatarUrl, discountPercentage);
        }

        return new Guest(fullName, dob, email, avatarUrl);
    }

    private String normalizeAvatarUrl(String avatarUrl) {
        if (avatarUrl == null || avatarUrl.isBlank()) {
            return DEFAULT_GUEST_AVATAR_URL;
        }

        return avatarUrl.trim();
    }

    // Parses optional ISO date values from the CSV file
    private LocalDate parseDate(String value) {
        if (value.isBlank()) {
            return null;
        }

        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("date of birth must use yyyy-MM-dd");
        }
    }

    // Parses optional discount values and defaults blank values to zero
    private BigDecimal parseDiscount(String value) {
        if (value.isBlank()) {
            return BigDecimal.ZERO;
        }

        try {
            return new BigDecimal(value);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("discount must be a number");
        }
    }
}