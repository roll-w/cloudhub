package org.huel.cloudhub.common;

/**
 * @author RollW
 */
public abstract class SystemRuntimeException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String message;

    public SystemRuntimeException(ErrorCode errorCode) {
        this(errorCode, errorCode.toString());
    }

    public SystemRuntimeException(ErrorCode errorCode, String message) {
        super(errorCode.toString());
        this.errorCode = errorCode;
        this.message = message;
    }

    public SystemRuntimeException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.message = message;
        this.errorCode = errorCode;
    }

    public SystemRuntimeException(Throwable cause) {
        super(cause);
        this.errorCode = ErrorCode.getErrorFromThrowable(cause);
        this.message = cause.toString();
    }

    public SystemRuntimeException(ErrorCode errorCode, Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
        this.message = cause.toString();
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
