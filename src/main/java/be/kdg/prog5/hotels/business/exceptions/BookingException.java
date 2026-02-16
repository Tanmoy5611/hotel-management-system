package be.kdg.prog5.hotels.business.exceptions;

public class BookingException extends RuntimeException {

    private final String code;

    public BookingException(String code) {
        super(code);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}