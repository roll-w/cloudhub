package org.huel.cloudhub.client.disk.domain.storagesearch.common;

/**
 * @author RollW
 */
public class SearchExpressionException extends RuntimeException {
    public SearchExpressionException() {
        super();
    }

    public SearchExpressionException(String message) {
        super(message);
    }

    public SearchExpressionException(String message, Throwable cause) {
        super(message, cause);
    }

    public SearchExpressionException(Throwable cause) {
        super(cause);
    }
}
