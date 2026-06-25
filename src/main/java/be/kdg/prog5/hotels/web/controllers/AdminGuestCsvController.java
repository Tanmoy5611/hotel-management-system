package be.kdg.prog5.hotels.web.controllers;

import be.kdg.prog5.hotels.business.guest.GuestCsvImportService;
import org.springframework.security.access.prepost.PreAuthorize;
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
                                  Model model) throws IOException {
        var result = guestCsvImportService.startImport(file.getBytes(), file.getOriginalFilename());
        model.addAttribute("inProgress", result.inProgress());
        model.addAttribute("fileName", result.fileName());
        model.addAttribute("errorMessage", result.errorMessage());

        return "admin-guests-csv";
    }
}