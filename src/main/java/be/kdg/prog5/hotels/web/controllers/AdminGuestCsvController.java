package be.kdg.prog5.hotels.web.controllers;

import be.kdg.prog5.hotels.business.GuestCsvImportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@PreAuthorize("hasRole('ADMIN')")
public class AdminGuestCsvController {

    private static final Logger log = LoggerFactory.getLogger(AdminGuestCsvController.class);

    private final GuestCsvImportService guestCsvImportService;

    // Injects the async CSV import service used by the upload page
    public AdminGuestCsvController(GuestCsvImportService guestCsvImportService) {
        this.guestCsvImportService = guestCsvImportService;
    }

    // Shows the admin CSV upload form before an import starts
    @GetMapping("/admin/guests-csv")
    public String showUploadPage(Model model) {
        model.addAttribute("inProgress", false);
        return "admin-guests-csv";
    }

    // Starts the CSV import and returns immediately while processing continues in the background
    @PostMapping("/admin/guests-csv")
    public String uploadGuestsCsv(@RequestParam("guests_csv") MultipartFile file,
                                  Authentication authentication,
                                  Model model) throws IOException {
        if (file.isEmpty()) {
            model.addAttribute("inProgress", false);
            model.addAttribute("errorMessage", "Please choose a CSV file before uploading.");
            return "admin-guests-csv";
        }

        log.info("Admin {} started guest CSV upload: {}", authentication.getName(), file.getOriginalFilename());

        guestCsvImportService.importGuests(file.getBytes(), authentication.getName());

        model.addAttribute("inProgress", true);
        model.addAttribute("fileName", file.getOriginalFilename());

        return "admin-guests-csv";
    }
}