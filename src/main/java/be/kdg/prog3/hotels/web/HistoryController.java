package be.kdg.prog3.hotels.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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