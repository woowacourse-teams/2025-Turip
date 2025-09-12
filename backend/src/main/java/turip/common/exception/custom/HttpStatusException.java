package turip.common.exception.custom;

import org.springframework.http.HttpStatus;
import turip.common.exception.ErrorTag;

public class HttpStatusException extends RuntimeException {

    private final HttpStatus status;
    private final ErrorTag errorTag;

    public HttpStatusException(HttpStatus status, ErrorTag errorTag) {
        super(errorTag.getMessage());
        this.status = status;
        this.errorTag = errorTag;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
