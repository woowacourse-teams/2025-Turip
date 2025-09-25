package turip.common.exception.custom;

import org.springframework.http.HttpStatus;
import turip.common.exception.ErrorTag;

public class NotFoundException extends HttpStatusException {

    public NotFoundException(ErrorTag errorTag) {
        super(HttpStatus.NOT_FOUND, errorTag);
    }
}
