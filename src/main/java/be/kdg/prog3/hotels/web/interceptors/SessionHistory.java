package be.kdg.prog3.hotels.web.interceptors;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

// Stores a user's page-visit history within one browser session
@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SessionHistory {

    private final List<String> history = new LinkedList<>();
    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void add(String page) {
        String timestamp = LocalDateTime.now().format(formatter);
        history.add(timestamp + " — " + page);
    }

    public List<String> getHistory() {
        return history;
    }
}