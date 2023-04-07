package org.huel.cloudhub.web;

import space.lingu.Nullable;

import java.util.List;

/**
 * @author RollW
 */
public interface ErrorCodeFinder {
    /**
     * Find the error code of the exception.
     *
     * @param e the exception
     * @return the error code
     */
    default ErrorCode fromThrowable(Throwable e) {
        return fromThrowable(e, CommonErrorCode.ERROR_UNKNOWN);
    }

    ErrorCode fromThrowable(Throwable e, ErrorCode defaultErrorCode);

    ErrorCode findErrorCode(String codeValue);

    List<ErrorCode> listErrorCodes();

    @Nullable
    static <T extends ErrorCode> T from(T[] codes, String codeValue) {
        if (codes == null || codeValue == null) {
            return null;
        }
        for (T errorCode : codes) {
            if (errorCode.getCode().equals(codeValue)) {
                return errorCode;
            }
        }
        return null;
    }
}
