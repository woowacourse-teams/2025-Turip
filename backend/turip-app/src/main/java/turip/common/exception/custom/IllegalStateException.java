package turip.common.exception.custom;

import turip.common.exception.ErrorTag;

public class IllegalStateException extends RuntimeException {

    private final ErrorTag errorTag;

    public IllegalStateException(final ErrorTag errorTag) {
        super(errorTag.getMessage());
        this.errorTag = errorTag;
    }

    public IllegalStateException(final ErrorTag errorTag, final Throwable cause) {
        super(errorTag.getMessage(), cause);
        this.errorTag = errorTag;
    }

    public ErrorTag getErrorTag() {
        return errorTag;
    }
}
