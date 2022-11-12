package org.huel.cloudhub.file.server;

import org.huel.cloudhub.common.ErrorCode;
import org.huel.cloudhub.common.SystemRuntimeException;

/**
 * @author RollW
 */
public class FileServerRuntimeException extends SystemRuntimeException {
    public FileServerRuntimeException(ErrorCode errorCode) {
        super(errorCode);
    }

    public FileServerRuntimeException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public FileServerRuntimeException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }

    public FileServerRuntimeException(Throwable cause) {
        super(cause);
    }

    public FileServerRuntimeException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
