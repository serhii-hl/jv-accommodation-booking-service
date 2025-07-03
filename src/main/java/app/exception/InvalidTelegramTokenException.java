package app.exception;

public class InvalidTelegramTokenException extends RuntimeException {
    public InvalidTelegramTokenException(String message) {
        super(message);
    }
}
