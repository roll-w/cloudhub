package org.huel.cloudhub.web;

import org.huel.cloudhub.common.BusinessRuntimeException;
import org.huel.cloudhub.common.ErrorCode;
import org.huel.cloudhub.common.ErrorCodeFinder;
import org.huel.cloudhub.common.ErrorCodeMessageProvider;
import space.lingu.NonNull;
import space.lingu.light.LightRuntimeException;

import java.util.List;

/**
 *
 * @author RollW
 */
public enum DataErrorCode implements ErrorCode, ErrorCodeFinder, ErrorCodeMessageProvider {
    ERROR_DATABASE("B0100", 500),
    ERROR_COLUMN_EXISTED("B0101", 500),
    ERROR_COLUMN_NOT_EXIST("B0102", 500),
    ERROR_DATA_NOT_EXIST("B0103", 404),
    ERROR_DATA_EXISTED("B0104", 201),

    ;

    private final String value;
    private final int status;

    DataErrorCode(String value, int status) {
        this.value = value;
        this.status = status;
    }

    @Override
    public String toString() {
        return "DataError: %s, code: %s".formatted(name(), getCode());
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
        return false;
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
        if (e instanceof LightRuntimeException) {
            return ERROR_DATABASE;
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

    public static ErrorCodeFinder getFinderInstance() {
        return ERROR_DATABASE;
    }
}
