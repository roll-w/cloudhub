package org.huel.cloudhub.client.disk.domain.systembased;

import org.huel.cloudhub.web.BusinessRuntimeException;
import org.huel.cloudhub.web.ErrorCode;

/**
 * @author RollW
 */
public class SystemResourceException extends BusinessRuntimeException {
    public SystemResourceException(ErrorCode errorCode) {
        super(errorCode);
    }

    public SystemResourceException(ErrorCode errorCode, String message, Object... args) {
        super(errorCode, message, args);
    }

    public SystemResourceException(ErrorCode errorCode, String message, Throwable cause, Object... args) {
        super(errorCode, message, cause, args);
    }

    public SystemResourceException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
