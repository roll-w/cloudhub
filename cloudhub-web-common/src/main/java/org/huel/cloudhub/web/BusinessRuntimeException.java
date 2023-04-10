package org.huel.cloudhub.web;

import space.lingu.NonNull;

import java.text.MessageFormat;

/**
 * Business Runtime Exception. Can be used to pass user prompts into responses.
 * If it is an internal system error, use other Exception classes.
 * <p>
 * Avoid using this class directly to throw exception
 * and try to use inherited classes instead.
 *
 * @author RollW
 */
public class BusinessRuntimeException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String message;
    private final Object[] args;

    public BusinessRuntimeException(ErrorCode errorCode) {
        this(errorCode, errorCode.toString());
    }

    public BusinessRuntimeException(ErrorCode errorCode, String message, Object... args) {
        super(errorCode.toString());
        this.errorCode = errorCode;
        this.message = message;
        this.args = args;
    }

    public BusinessRuntimeException(ErrorCode errorCode, String message, Throwable cause, Object... args) {
        super(message, cause);
        this.message = message;
        this.errorCode = errorCode;
        this.args = args;
    }

    public BusinessRuntimeException(ErrorCode errorCode, Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
        this.message = cause.toString();
        this.args = new Object[0];
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @NonNull
    public Object[] getArgs() {
        return args;
    }

    public String getFormattedMessage() {
        if (args == null || args.length == 0) {
            return message;
        }
        try {
            return MessageFormat.format(message, args);
        } catch (Exception e) {
            return message;
        }
    }
}
