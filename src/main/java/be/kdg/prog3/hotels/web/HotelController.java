package be.kdg.prog3.hotels.web;

import be.kdg.prog3.hotels.business.HotelService;
import be.kdg.prog3.hotels.data.DataFactory;
import be.kdg.prog3.hotels.domain.Hotel;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

// this controller handles all web requests for the hotels page
@Controller
@RequestMapping("/hotels")        // base url for all methods in this controller
public class HotelController {
    // Logger for debugging messages
    private static final Logger log = LoggerFactory.getLogger(HotelController.class);
    private final HotelService hotelService;   // injecting the HotelService to access business logic

    // Constructor injection (Spring will automatically provide the HotelService bean)
    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    // method of showing all Hotels (list) + filter them based on: minStars +  opened date
    @GetMapping
    public String list(@RequestParam(name = "minStars", required = false) Integer minStars,
                       @RequestParam(name = "opened", required = false) String dateIn,
                       Model model) {
        // Log the parameters to see what filter values passed
        log.debug("Listing hotels with minStars={} and openedAfter={}", minStars, dateIn);

        // If no filter is given, show all hotels
        if (minStars == null) {
            model.addAttribute("hotels", hotelService.getAllHotels());

        // If minStars is given, call service method to filter by stars and optional date
        } else {
            model.addAttribute("hotels", hotelService.getHotelsByMinStarsAndDate(minStars, dateIn));
        }
        // Return the name of the Thymeleaf template (hotels.html)
        return "hotels";
    }

    // Add Hotel Form
    // This method shows the "Add Hotel" form when visits "/hotels/add"
    @GetMapping("/add")
    public String addForm(Model model) {
        // Create empty Hotel object to bind form fields
        model.addAttribute("hotel", new Hotel()); // lowercase name convention
        return "add-hotel";   // Return the add-hotel.html template

    }

    // Save  new Hotel
    // This method processes the form submission for adding  new hotel
    @PostMapping("/add")
    public String addSubmit(@ModelAttribute("hotel") @Valid Hotel hotel,
                            BindingResult bindingResult) {
        // If the form has validation errors, reload the same page
        if (bindingResult.hasErrors()) {
            return "add-hotel";
        }

        // Log and save the new hotel through the service layer
        log.debug("Creating hotel: {}", hotel);
        hotelService.createdHotel(hotel);

        // Redirect to /hotels after successfully adding a new hotel
        return "redirect:/hotels";
    }


    @GetMapping("/{id}")
    public String showHotelDetails(@PathVariable String id, Model model) {

        // Search for the hotel with the matching id (case-insensitive)
        var hotel = DataFactory.hotels.stream()
                .filter(h -> h.getId().equalsIgnoreCase(id))
                .findFirst()
                .orElse(null);

        // If no hotel found, redirect to main hotels list
        if (hotel == null) {
            return "redirect:/hotels";
        }

        // Add the found hotel to the model so Thymeleaf can display it
        model.addAttribute("hotel", hotel);
        return "hotel-detail";
    }


}