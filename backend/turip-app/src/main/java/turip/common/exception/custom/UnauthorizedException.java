package turip.common.exception.custom;

import org.springframework.http.HttpStatus;
import turip.common.exception.ErrorTag;

public class UnauthorizedException extends HttpStatusException {

    public UnauthorizedException(ErrorTag errorTag) {
        super(HttpStatus.UNAUTHORIZED, errorTag);
    }

    public UnauthorizedException(ErrorTag errorTag, Throwable cause) {
        super(HttpStatus.UNAUTHORIZED, errorTag, cause);
    }
}
