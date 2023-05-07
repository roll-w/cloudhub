package org.huel.cloudhub.client.disk.domain.tag.common;

import org.huel.cloudhub.web.BusinessRuntimeException;
import org.huel.cloudhub.web.ErrorCode;

/**
 * @author RollW
 */
public class ContentTagException extends BusinessRuntimeException {
    public ContentTagException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ContentTagException(ErrorCode errorCode, String message, Object... args) {
        super(errorCode, message, args);
    }

    public ContentTagException(ErrorCode errorCode, String message, Throwable cause, Object... args) {
        super(errorCode, message, cause, args);
    }

    public ContentTagException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
