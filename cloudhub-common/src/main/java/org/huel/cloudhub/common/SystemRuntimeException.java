package org.huel.cloudhub.common;

/**
 * @author RollW
 */
public class SystemRuntimeException extends RuntimeException {
    public SystemRuntimeException() {
        super();
    }

    public SystemRuntimeException(String message) {
        super(message);
    }

    public SystemRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public SystemRuntimeException(Throwable cause) {
        super(cause);
    }

    protected SystemRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
