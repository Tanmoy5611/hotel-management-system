package be.kdg.prog3.hotels.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// used only for simple navigation at the start of the web app
@Controller
public class NavController {
    // When user visits the root URL ("/"), redirect them to the hotels page
    @GetMapping("/")
    public String home() {
        // Redirect to the main hotels list
        return "redirect:/hotels";
    }
}
