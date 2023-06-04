package org.huel.cloudhub.client.disk.domain.usergroup.common;

import org.huel.cloudhub.web.BusinessRuntimeException;
import org.huel.cloudhub.web.ErrorCode;

/**
 * @author RollW
 */
public class UserGroupException extends BusinessRuntimeException {
    public UserGroupException(ErrorCode errorCode) {
        super(errorCode);
    }

    public UserGroupException(ErrorCode errorCode, String message, Object... args) {
        super(errorCode, message, args);
    }

    public UserGroupException(ErrorCode errorCode, String message, Throwable cause, Object... args) {
        super(errorCode, message, cause, args);
    }

    public UserGroupException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
