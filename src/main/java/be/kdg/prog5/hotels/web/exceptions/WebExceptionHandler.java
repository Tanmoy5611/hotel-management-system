package be.kdg.prog5.hotels.web.exceptions;

import be.kdg.prog5.hotels.business.exceptions.RoomNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.NoHandlerFoundException;

// Handles exceptions for normal Thymeleaf pages and returns HTML error pages
@ControllerAdvice
public class WebExceptionHandler {

    // Shows 404 page when a room does not exist
    @ExceptionHandler(RoomNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleRoomNotFound(RoomNotFoundException ex, Model model) {
        model.addAttribute("message", ex.getMessage());
        return "error/404";
    }

    // Shows 404 page when the user opens a URL that does not exist
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(Model model) {
        model.addAttribute("message", "Page not found");
        return "error/404";
    }

    // Shows 400 page for bad input, like invalid filter or form values
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBadRequest(IllegalArgumentException ex, Model model) {
        model.addAttribute("message", ex.getMessage());
        return "error/400";
    }

    // Shows 500 page for unexpected errors
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGeneralError(Exception ex, Model model) {
        ex.printStackTrace(); // debug

        model.addAttribute("message", "Unexpected error occurred");
        return "error/500";
    }
}