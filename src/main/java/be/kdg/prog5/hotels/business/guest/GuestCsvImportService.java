package be.kdg.prog5.hotels.business.guest;

import be.kdg.prog5.hotels.business.security.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class GuestCsvImportService {

    private static final Logger log = LoggerFactory.getLogger(GuestCsvImportService.class);

    private final GuestCsvImportWorker guestCsvImportWorker;
    private final SecurityService securityService;

    public GuestCsvImportService(GuestCsvImportWorker guestCsvImportWorker,
                                 SecurityService securityService) {
        this.guestCsvImportWorker = guestCsvImportWorker;
        this.securityService = securityService;
    }

    public CsvImportResult startImport(byte[] csvBytes, String fileName) {
        // Empty file validation stays in the service instead of the controller
        if (csvBytes.length == 0) {
            return new CsvImportResult(false, null, "Please choose a CSV file before uploading.");
        }

        // Store the admin email before starting the async import thread
        String ownerEmail = securityService.getLoggedInUsername();
        log.info("Admin {} started guest CSV upload: {}", ownerEmail, fileName);
        guestCsvImportWorker.importGuests(csvBytes, ownerEmail);

        // The real import continues in the background
        return new CsvImportResult(true, fileName, null);
    }
}