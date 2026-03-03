package turip.common.exception.custom;

import org.springframework.http.HttpStatus;
import turip.common.exception.ErrorTag;

public class ForbiddenException extends HttpStatusException {

    public ForbiddenException(ErrorTag errorTag) {
        super(HttpStatus.FORBIDDEN, errorTag);
    }

    public ForbiddenException(ErrorTag errorTag, Throwable cause) {
        super(HttpStatus.FORBIDDEN, errorTag, cause);
    }
}
