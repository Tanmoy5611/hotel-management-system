package be.kdg.prog3.hotels.web.interceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

// Intercepts every request and records the visited URL and time.

@Component
public class HistoryInterceptor implements HandlerInterceptor {

    private final SessionHistory sessionHistory;

    public HistoryInterceptor(SessionHistory sessionHistory) {
        this.sessionHistory = sessionHistory;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {
        String uri = request.getRequestURI();
        // avoid infinite loop when user already on /history
        if (!uri.contains("/history") &&
                !uri.contains("/css") &&
                !uri.contains("/images") &&
                !uri.contains("/webjars") &&
                !uri.contains("/error") &&
                !uri.contains("/js") &&
                !uri.contains("/favicon")) {
            sessionHistory.add(uri);
        }
        return true;
    }
}