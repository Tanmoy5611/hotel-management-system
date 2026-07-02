package be.kdg.prog5.hotels.business.ai;

// Business exception used when the Python AI service cannot be reached
public class AiServiceUnavailableException extends RuntimeException {
    public AiServiceUnavailableException(String message, Throwable cause) {
        // Preserve the original cause for logging and debugging
        super(message, cause);
    }
}