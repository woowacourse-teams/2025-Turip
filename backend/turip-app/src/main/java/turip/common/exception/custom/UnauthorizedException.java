package turip.common.exception.custom;

import org.springframework.http.HttpStatus;
import turip.common.exception.ErrorTag;

public class UnauthorizedException extends HttpStatusException {

    public UnauthorizedException(ErrorTag errorTag) {
        super(HttpStatus.UNAUTHORIZED, errorTag);
    }
}
