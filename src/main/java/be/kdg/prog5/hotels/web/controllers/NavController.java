package be.kdg.prog5.hotels.web.controllers;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// Controller responsible for simple navigation redirects
@Controller
public class NavController {
    // When user visits the root URL ("/"), redirect them to the home page
    @GetMapping("/")
    public String root() {
        return "redirect:/home";
    }
}