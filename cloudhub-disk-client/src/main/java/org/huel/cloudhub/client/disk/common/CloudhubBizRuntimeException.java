package org.huel.cloudhub.client.disk.common;

import org.huel.cloudhub.web.BusinessRuntimeException;
import org.huel.cloudhub.web.ErrorCode;

/**
 * @author RollW
 */
public class CloudhubBizRuntimeException extends BusinessRuntimeException {
    public CloudhubBizRuntimeException(ErrorCode errorCode) {
        super(errorCode);
    }

    public CloudhubBizRuntimeException(ErrorCode errorCode, String message, Object... args) {
        super(errorCode, message, args);
    }

    public CloudhubBizRuntimeException(ErrorCode errorCode, String message, Throwable cause, Object... args) {
        super(errorCode, message, cause, args);
    }

    public CloudhubBizRuntimeException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
