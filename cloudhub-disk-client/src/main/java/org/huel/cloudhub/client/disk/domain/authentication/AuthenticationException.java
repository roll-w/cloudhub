package org.huel.cloudhub.client.disk.domain.authentication;

import org.huel.cloudhub.web.BusinessRuntimeException;
import org.huel.cloudhub.web.ErrorCode;

/**
 * @author RollW
 */
public class AuthenticationException extends BusinessRuntimeException {
    public AuthenticationException(ErrorCode errorCode) {
        super(errorCode);
    }

    public AuthenticationException(ErrorCode errorCode, String message, Object... args) {
        super(errorCode, message, args);
    }

    public AuthenticationException(ErrorCode errorCode, String message, Throwable cause, Object... args) {
        super(errorCode, message, cause, args);
    }

    public AuthenticationException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
