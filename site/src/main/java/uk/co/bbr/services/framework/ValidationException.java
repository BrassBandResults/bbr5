package uk.co.bbr.services.framework;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
