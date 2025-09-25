package turip.common.exception.custom;

import org.springframework.http.HttpStatus;
import turip.common.exception.ErrorTag;

public class BadRequestException extends HttpStatusException {

    public BadRequestException(ErrorTag errorTag) {
        super(HttpStatus.BAD_REQUEST, errorTag);
    }
}
