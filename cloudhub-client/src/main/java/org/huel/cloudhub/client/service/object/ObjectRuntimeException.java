package org.huel.cloudhub.client.service.object;

import org.huel.cloudhub.common.ErrorCode;
import org.huel.cloudhub.common.SystemRuntimeException;

/**
 * @author RollW
 */
public class ObjectRuntimeException extends SystemRuntimeException {
    public ObjectRuntimeException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ObjectRuntimeException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public ObjectRuntimeException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }

    public ObjectRuntimeException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    public ObjectRuntimeException(Throwable cause) {
        super(cause);
    }
}
