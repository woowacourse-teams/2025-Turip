package turip.exception.custom;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends HttpStatusException {

    public ForbiddenException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }
}
