package be.kdg.prog3.hotels.web.controllers;
import be.kdg.prog3.hotels.web.interceptors.SessionHistory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

// responsible for displaying user's navigation history (collected by SessionHistory interceptor)
@Controller
public class HistoryController {

    private final SessionHistory sessionHistory;

    public HistoryController(SessionHistory sessionHistory) {
        this.sessionHistory = sessionHistory;
    }

    @GetMapping("/history")
    public String showHistory(Model model) {
        model.addAttribute("history", sessionHistory.getHistory());

        return "history";     // refers to history.html
    }
}