package turip.common.exception.custom;

import org.springframework.http.HttpStatus;
import turip.common.exception.ErrorTag;

public class ConflictException extends HttpStatusException {

    public ConflictException(ErrorTag errorTag) {
        super(HttpStatus.CONFLICT, errorTag);
    }

    public ConflictException(ErrorTag errorTag, Throwable cause) {
        super(HttpStatus.CONFLICT, errorTag, cause);
    }
}
