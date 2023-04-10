package org.huel.cloudhub.web;

import space.lingu.NonNull;
import space.lingu.Nullable;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * @author RollW
 */
public enum IoErrorCode implements ErrorCode, ErrorCodeFinder, ErrorCodeMessageProvider {
    SUCCESS(SUCCESS_CODE, 200),
    ERROR_IO("A0700", 500),
    ERROR_FILE_NOT_FOUND("A0701", 404),
    ERROR_FILE_EXISTED("A0702"),
    ERROR_FILE_UPLOAD("A0703", 401),
    ;


    private final String value;
    private final int status;

    IoErrorCode(String value, int status) {
        this.value = value;
        this.status = status;
    }

    IoErrorCode(String value) {
        this.value = value;
        this.status = 401;
    }

    @Override
    public String toString() {
        if (this == SUCCESS) {
            return "SUCCESS";
        }

        return "IoError: %s, code: %s".formatted(name(), getCode());
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
        if (e instanceof FileNotFoundException) {
            return ERROR_FILE_NOT_FOUND;
        }
        if (e instanceof IOException) {
            return ERROR_IO;
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
    private static IoErrorCode nullableFrom(String value) {
        for (IoErrorCode errorCode : values()) {
            if (errorCode.value.equals(value)) {
                return errorCode;
            }
        }
        return null;
    }

    @Nullable
    public static IoErrorCode from(String value) {
        return nullableFrom(value);
    }

    public static ErrorCodeFinder getFinderInstance() {
        return SUCCESS;
    }
}
