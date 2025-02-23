package org.example.docmeet.exception;

@Deprecated(forRemoval = true)
public class NotFoundException extends RuntimeException {
    public NotFoundException() {
        this(null, null);
    }

    public NotFoundException(final String message) {
        this(message, null);
    }

    public NotFoundException(final Throwable cause) {
        this(cause != null ? cause.getMessage() : null, cause);
    }

    public NotFoundException(final String message, final Throwable cause) {
        super(message);
        if (cause != null) super.initCause(cause);
    }
}
