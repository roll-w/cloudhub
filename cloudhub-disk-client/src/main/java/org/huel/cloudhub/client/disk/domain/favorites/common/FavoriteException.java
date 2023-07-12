package org.huel.cloudhub.client.disk.domain.favorites.common;

import org.huel.cloudhub.web.BusinessRuntimeException;
import org.huel.cloudhub.web.ErrorCode;

/**
 * @author RollW
 */
public class FavoriteException extends BusinessRuntimeException {
    public FavoriteException(ErrorCode errorCode) {
        super(errorCode);
    }

    public FavoriteException(ErrorCode errorCode, String message, Object... args) {
        super(errorCode, message, args);
    }

    public FavoriteException(ErrorCode errorCode, String message, Throwable cause, Object... args) {
        super(errorCode, message, cause, args);
    }

    public FavoriteException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
