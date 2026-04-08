package be.kdg.prog5.hotels.web.exceptions;

import be.kdg.prog5.hotels.business.exceptions.RoomNotFoundException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.NoHandlerFoundException;

// for MVC / Thymeleaf ->  @ControllerAdvice (HTML pages)
@ControllerAdvice
public class WebExceptionHandler {

    // 404 - Not found (custom exceptions)
    @ExceptionHandler(RoomNotFoundException.class)
    public String handleRoomNotFound(RoomNotFoundException ex, Model model) {
        model.addAttribute("message", ex.getMessage());
        return "error/404";
    }

    // 404 - URL not found
    @ExceptionHandler(NoHandlerFoundException.class)
    public String handleNotFound(Model model) {
        model.addAttribute("message", "Page not found");
        return "error/404";
    }

    // 400 - Bad request
    @ExceptionHandler(IllegalArgumentException.class)
    public String handleBadRequest(IllegalArgumentException ex, Model model) {
        model.addAttribute("message", ex.getMessage());
        return "error/400";
    }

    // 500 - fallback
    @ExceptionHandler(Exception.class)
    public String handleGeneralError(Exception ex, Model model) {
        model.addAttribute("message", "Something went wrong");
        return "error/500";
    }
}