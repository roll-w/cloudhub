package org.huel.cloudhub.client.disk.domain.userstorage.common;

import org.huel.cloudhub.common.BusinessRuntimeException;
import org.huel.cloudhub.common.ErrorCode;

/**
 * @author RollW
 */
public class StorageException extends BusinessRuntimeException {
    public StorageException(ErrorCode errorCode) {
        super(errorCode);
    }

    public StorageException(ErrorCode errorCode, String message, Object... args) {
        super(errorCode, message, args);
    }

    public StorageException(ErrorCode errorCode, String message, Throwable cause, Object... args) {
        super(errorCode, message, cause, args);
    }

    public StorageException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
