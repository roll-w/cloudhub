package org.huel.cloudhub.client.disk.domain.user.common;


import org.huel.cloudhub.web.BusinessRuntimeException;
import org.huel.cloudhub.web.ErrorCode;

/**
 * @author RollW
 */
public class UserViewException extends BusinessRuntimeException {
    public UserViewException(ErrorCode errorCode) {
        super(errorCode);
    }

    public UserViewException(ErrorCode errorCode, String message, Object... args) {
        super(errorCode, message, args);
    }

    public UserViewException(ErrorCode errorCode, String message, Throwable cause, Object... args) {
        super(errorCode, message, cause, args);
    }

    public UserViewException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
