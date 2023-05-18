package org.huel.cloudhub.meta.server.service.file;

/**
 * @author RollW
 */
public class FileServerException extends RuntimeException {
    public FileServerException() {
        super();
    }

    public FileServerException(String message) {
        super(message);
    }

    public FileServerException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileServerException(Throwable cause) {
        super(cause);
    }
}
