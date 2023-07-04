package org.cloudhub.util;

/**
 * @author RollW
 */
public class TimeExpressionException extends RuntimeException {
    public TimeExpressionException() {
        super();
    }

    public TimeExpressionException(String message) {
        super(message);
    }

    public TimeExpressionException(String message, Throwable cause) {
        super(message, cause);
    }

    public TimeExpressionException(Throwable cause) {
        super(cause);
    }
}
