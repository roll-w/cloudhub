package org.huel.cloudhub.client.disk.domain.storagepermission.common;

import org.huel.cloudhub.web.BusinessRuntimeException;
import org.huel.cloudhub.web.ErrorCode;

/**
 * @author RollW
 */
public class StoragePermissionException extends BusinessRuntimeException {
    public StoragePermissionException(ErrorCode errorCode) {
        super(errorCode);
    }

    public StoragePermissionException(ErrorCode errorCode, String message, Object... args) {
        super(errorCode, message, args);
    }

    public StoragePermissionException(ErrorCode errorCode, String message, Throwable cause, Object... args) {
        super(errorCode, message, cause, args);
    }

    public StoragePermissionException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
