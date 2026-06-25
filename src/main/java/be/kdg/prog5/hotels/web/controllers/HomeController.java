package be.kdg.prog5.hotels.web.controllers;

import be.kdg.prog5.hotels.business.home.HomeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

// Controller responsible for preparing dashboard style data
@Controller
public class HomeController {

    private static final Logger log = LoggerFactory.getLogger(HomeController.class);

    private final HomeService homeService;

    public HomeController(HomeService homeService) {
        this.homeService = homeService;
    }

    //  Populates model with hotel data for home view
    @GetMapping("/home")
    public String home(Model model) {
        log.debug("Loading home page");

        var homePage = homeService.getHomePage();
        model.addAttribute("featuredHotels", homePage.featuredHotels());
        model.addAttribute("beachSpaHotels", homePage.beachSpaHotels());
        model.addAttribute("cityHotels", homePage.cityHotels());
        model.addAttribute("bestValueRooms", homePage.bestValueRooms());
        model.addAttribute("premiumRooms", homePage.premiumRooms());
        model.addAttribute("topPickedRooms", homePage.topPickedRooms());

        return "home";
    }
}