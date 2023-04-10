package org.huel.cloudhub.web;

import space.lingu.NonNull;
import space.lingu.Nullable;

import java.util.List;

/**
 * @author RollW
 */
public enum CommonErrorCode implements ErrorCode, ErrorCodeFinder, ErrorCodeMessageProvider {
    SUCCESS(SUCCESS_CODE, 200),
    ERROR_EXCEPTION("B1000", 500),
    ERROR_NOT_FOUND("B1001", 404),
    ERROR_NULL("B1002", 500),
    ERROR_UNKNOWN("D0000", 500),
    ;


    private final String value;
    private final int status;

    CommonErrorCode(String value, int status) {
        this.value = value;
        this.status = status;
    }

    @Override
    public String toString() {
        if (this == SUCCESS) {
            return "SUCCESS";
        }

        return "CommonError: %s, code: %s".formatted(name(), getCode());
    }

    @NonNull
    @Override
    public String getCode() {
        return value;
    }

    @NonNull
    @Override
    public String getName() {
        return name();
    }

    @Override
    public boolean success() {
        return this == SUCCESS;
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public ErrorCode fromThrowable(Throwable e, ErrorCode defaultErrorCode) {
        if (e instanceof BusinessRuntimeException sys) {
            return sys.getErrorCode();
        }

        return null;
    }

    @Override
    public ErrorCode findErrorCode(String codeValue) {
        return ErrorCodeFinder.from(values(), codeValue);
    }

    private static final List<ErrorCode> CODES = List.of(values());

    @Override
    public List<ErrorCode> listErrorCodes() {
        return CODES;
    }

    @Nullable
    private static CommonErrorCode nullableFrom(String value) {
        for (CommonErrorCode errorCode : values()) {
            if (errorCode.value.equals(value)) {
                return errorCode;
            }
        }
        return null;
    }

    @NonNull
    public static CommonErrorCode from(String value) {
        CommonErrorCode errorCode = nullableFrom(value);
        if (errorCode == null) {
            return ERROR_UNKNOWN;
        }
        return errorCode;
    }
}