package be.kdg.prog5.hotels.web.controllers;

import be.kdg.prog5.hotels.business.hotel.HotelService;
import be.kdg.prog5.hotels.domain.Hotel;
import be.kdg.prog5.hotels.viewmodel.HotelForm;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

// this controller handles all web requests for the hotels page
@Controller
@RequestMapping("/hotels")          // base url for all methods in this controller
public class HotelController {        // All URLs in this controller start with /hotels

    // Logger for debugging messages in console
    private static final Logger log = LoggerFactory.getLogger(HotelController.class);

    private final HotelService hotelService;      // injecting the HotelService to access business logic

    // Constructor injection (Spring will automatically provide the HotelService bean)
    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    /// List hotels + filters
    // method of showing all Hotels (list) + filter them based on: minStars + opened date
    // The controller only orchestrates which business method to call based on user input
    @GetMapping
    public String list(@RequestParam(name = "minStars", required = false) Integer minStars,
                       @RequestParam(name = "opened", required = false)
                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate opened,
                       @RequestParam(name = "name", required = false) String name,
                       @RequestParam(name = "sort", required = false) String sort,
                       Model model) {

        log.debug("Listing hotels with filters: minStars={}, opened={}, name='{}', sort={}",
                minStars, opened, name, sort);

        List<Hotel> hotels = hotelService.findHotels(minStars, opened, name, sort);

        // Add data to model for Thymeleaf template
        model.addAttribute("hotels", hotels);
        model.addAttribute("total", hotels.size());

        // keep values in UI
        model.addAttribute("selectedMinStars", minStars);
        model.addAttribute("selectedOpened", opened);
        model.addAttribute("selectedName", name);
        model.addAttribute("selectedSort", sort);

        // Return the name of the Thymeleaf template (hotels.html)
        return "hotels";
    }

    /// Add hotel with @PreAuthorize security
    // This method shows the "Add Hotel" form when visits "/hotels/add"
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/add")
    public String addForm(Model model) {
        log.debug("Loading add hotel form");

        // Create empty Hotel object to bind form fields using HotelForm class
        model.addAttribute("hotelForm", new HotelForm()); // lowercase name convention

        return "add-hotel";   // Return the add-hotel.html template
    }

    /// Save new Hotel with @PreAuthorize security
    // This method processes the HotelForm submission for adding new hotel
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add")
    public String addSubmit(@ModelAttribute("hotelForm") @Valid HotelForm hotelForm,
                            BindingResult bindingResult) {

        // Check form validation errors (from annotations in HotelForm)
        if (bindingResult.hasErrors()) {
            log.debug("Validation errors found while adding hotel: {}", bindingResult.getAllErrors());

            // Return same page, errors will be displayed under fields
            return "add-hotel";
        }

        hotelService.createHotel(
                hotelForm.getName(), hotelForm.getCity(), hotelForm.getCountry(), hotelForm.getOpenedOn(),
                hotelForm.getStars(), hotelForm.isHasSpa(), hotelForm.getImageUrl(), hotelForm.getDescription());

        // Redirect to /hotels after successfully adding a new hotel
        return "redirect:/hotels?created";
    }

    /// Hotel details
    //  Show 1 hotel + its rooms + guests per room
    @GetMapping("/{hotelId}")
    public String showHotelDetails(@PathVariable String hotelId, Model model) {
        log.debug("Loading hotel details for hotel {}", hotelId);

        var hotelDetails = hotelService.getHotelDetails(hotelId);

        // Add the found hotel to the model so Thymeleaf can display it
        model.addAttribute("hotel", hotelDetails.hotel());
        model.addAttribute("rooms", hotelDetails.rooms());
        model.addAttribute("guestsPerRoom", hotelDetails.guestsPerRoom());
        model.addAttribute("totalGuests", hotelDetails.totalGuests());

        return "hotel-detail";            // hotel-detail.html
    }

    /// Delete hotel With PreAuthorize security for ADMIN
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{hotelId}/delete")
    public String deleteHotel(@PathVariable String hotelId) {
        log.debug("Deleting hotel {}", hotelId);

        hotelService.deleteHotelByHotelId(hotelId);          // Business layer handles cascading / repo

        // return "redirect:/hotels";
        return "redirect:/hotels?deleted";

    }

    /// Hotel description edit by ADMin only
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{hotelId}/edit-description")
    public String editHotelDescriptionForm(@PathVariable String hotelId, Model model) {
        log.debug("Loading edit hotel description form for hotel {}", hotelId);

        Hotel hotel = hotelService.getHotelByHotelId(hotelId);

        model.addAttribute("hotel", hotel);
        return "edit-hotel-description";
    }

    /// Update hotel description by Admin only
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{hotelId}/edit-description")
    public String updateHotelDescription(@PathVariable String hotelId,
                                         @RequestParam String description) {
        log.debug("Updating hotel description for hotel {}", hotelId);

        hotelService.updateHotelDescription(hotelId, description);

        return "redirect:/hotels/" + hotelId;
    }
}