package org.huel.cloudhub.client.service.object;

import org.huel.cloudhub.common.ErrorCode;
import org.huel.cloudhub.common.BusinessRuntimeException;

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
