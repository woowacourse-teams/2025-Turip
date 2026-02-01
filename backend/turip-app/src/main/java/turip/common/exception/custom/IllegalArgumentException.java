package turip.common.exception.custom;

import turip.common.exception.ErrorTag;

public class IllegalArgumentException extends RuntimeException {

    private final ErrorTag errorTag;

    public IllegalArgumentException(final ErrorTag errorTag) {
        super(errorTag.getMessage());
        this.errorTag = errorTag;
    }

    public ErrorTag getErrorTag() {
        return errorTag;
    }
}
