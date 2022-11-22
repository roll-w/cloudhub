package org.huel.cloudhub.client.service.bucket;

import org.huel.cloudhub.common.ErrorCode;
import org.huel.cloudhub.common.SystemRuntimeException;

/**
 * @author RollW
 */
public class BucketRuntimeException extends SystemRuntimeException {
    public BucketRuntimeException(ErrorCode errorCode) {
        super(errorCode);
    }

    public BucketRuntimeException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public BucketRuntimeException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }

    public BucketRuntimeException(Throwable cause) {
        super(cause);
    }

    public BucketRuntimeException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
