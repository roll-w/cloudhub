package org.huel.cloudhub.meta.server.service.node;

/**
 * @author RollW
 */
public class NodeServerException extends RuntimeException {
    public NodeServerException() {
        super();
    }

    public NodeServerException(String message) {
        super(message);
    }

    public NodeServerException(String message, Throwable cause) {
        super(message, cause);
    }

    public NodeServerException(Throwable cause) {
        super(cause);
    }
}
