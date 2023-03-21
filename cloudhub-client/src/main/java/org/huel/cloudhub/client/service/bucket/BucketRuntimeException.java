package org.huel.cloudhub.client.service.bucket;

import org.huel.cloudhub.common.ErrorCode;
import org.huel.cloudhub.common.BusinessRuntimeException;

/**
 * @author RollW
 */
public class BucketRuntimeException extends BusinessRuntimeException {
    public BucketRuntimeException(ErrorCode errorCode) {
        super(errorCode);
    }

    public BucketRuntimeException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public BucketRuntimeException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }

    public BucketRuntimeException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
