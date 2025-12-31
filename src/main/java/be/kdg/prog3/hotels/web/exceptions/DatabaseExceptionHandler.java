package be.kdg.prog3.hotels.web.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class DatabaseExceptionHandler {

    private static final Logger log =
            LoggerFactory.getLogger(DatabaseExceptionHandler.class);

    @ExceptionHandler(DataAccessException.class)
    public String handleDatabaseException(
            DataAccessException ex,
            Model model
    ) {
        log.error("Database error occurred", ex);

        model.addAttribute("errorMessage",
                "A database error occurred. Please try again later.");

        return "error/database-error";
    }
}