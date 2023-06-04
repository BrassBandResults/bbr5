package uk.co.bbr.services.framework;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.BAD_REQUEST, reason="Validation Error")
public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
