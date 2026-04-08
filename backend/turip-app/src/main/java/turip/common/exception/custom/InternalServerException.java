package turip.common.exception.custom;

import org.springframework.http.HttpStatus;
import turip.common.exception.ErrorTag;

public class InternalServerException extends HttpStatusException {

    public InternalServerException(ErrorTag errorTag) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, errorTag);
    }

    public InternalServerException(ErrorTag errorTag, Throwable cause) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, errorTag, cause);
    }
}
