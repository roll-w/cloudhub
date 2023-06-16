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
    ERROR_ALREADY_EXISTS("B1002", 400),
    ERROR_NULL("B1003", 500),
    ERROR_SERVICE_UNAVAILABLE("B1004", 503),
    ERROR_NOT_IMPLEMENTED("B1005", 501),
    ERROR_INTERNAL("B1006", 500),
    ERROR_ILLEGAL_STATE("B1007", 500),
    ERROR_ILLEGAL_ARGUMENT("B1008", 400),
    ERROR_UNSUPPORTED_OPERATION("B1009", 400),
    ERROR_CANCELED("B1011", 400),
    ERROR_ABORTED("B1012", 400),
    ERROR_TIMEOUT("B1013", 408),
    ERROR_DATA_LOSS("B1014", 500),
    ERROR_OUT_OF_RANGE("B1015", 400),
    ERROR_RESOURCE_EXHAUSTED("B1016", 500),

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
