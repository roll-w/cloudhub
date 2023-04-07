package org.huel.cloudhub.objectstorage.service.object;

import org.huel.cloudhub.web.ErrorCode;
import org.huel.cloudhub.web.BusinessRuntimeException;

/**
 * @author RollW
 */
public class ObjectRuntimeException extends BusinessRuntimeException {
    public ObjectRuntimeException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ObjectRuntimeException(ErrorCode errorCode, String message, Object... args) {
        super(errorCode, message, args);
    }

    public ObjectRuntimeException(ErrorCode errorCode, String message, Throwable cause, Object... args) {
        super(errorCode, message, cause, args);
    }

    public ObjectRuntimeException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
