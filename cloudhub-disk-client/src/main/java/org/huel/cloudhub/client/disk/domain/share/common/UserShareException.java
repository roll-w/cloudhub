package org.huel.cloudhub.client.disk.domain.share.common;

import org.huel.cloudhub.web.BusinessRuntimeException;
import org.huel.cloudhub.web.ErrorCode;

/**
 * @author RollW
 */
public class UserShareException extends BusinessRuntimeException {
    public UserShareException(ErrorCode errorCode) {
        super(errorCode);
    }

    public UserShareException(ErrorCode errorCode, String message, Object... args) {
        super(errorCode, message, args);
    }

    public UserShareException(ErrorCode errorCode, String message, Throwable cause, Object... args) {
        super(errorCode, message, cause, args);
    }

    public UserShareException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
