package org.huel.cloudhub.meta.server.service.file;

/**
 * @author RollW
 */
public class FileDownloadingException extends RuntimeException {
    public FileDownloadingException() {
        super();
    }

    public FileDownloadingException(String message) {
        super(message);
    }

    public FileDownloadingException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileDownloadingException(Throwable cause) {
        super(cause);
    }
}
