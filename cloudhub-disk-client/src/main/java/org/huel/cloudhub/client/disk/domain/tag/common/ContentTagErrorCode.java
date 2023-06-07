package org.huel.cloudhub.client.disk.domain.tag.common;

import org.huel.cloudhub.web.BusinessRuntimeException;
import org.huel.cloudhub.web.ErrorCode;
import org.huel.cloudhub.web.ErrorCodeFinder;
import org.huel.cloudhub.web.ErrorCodeMessageProvider;
import space.lingu.NonNull;

import java.util.List;

/**
 *
 * @author RollW
 */
public enum ContentTagErrorCode implements ErrorCode, ErrorCodeFinder, ErrorCodeMessageProvider {
    ERROR_CONTENT_TAG("B3500", 400),
    ERROR_TAG_EXIST("B3501", 400),
    ERROR_TAG_NOT_EXIST("B3502", 404),
    ERROR_TAG_NAME_EMPTY("B3503", 400),

    ERROR_TAG_GROUP_EXIST("B3504", 400),
    ERROR_TAG_GROUP_NOT_EXIST("B3505", 404),
    ERROR_TAG_GROUP_NAME_EMPTY("B3506", 400),
    ERROR_TAG_GROUP_NAME_EXIST("B3507", 400),


    ;

    private final String value;
    private final int status;

    ContentTagErrorCode(String value, int status) {
        this.value = value;
        this.status = status;
    }

    @Override
    public String toString() {
        return "ContentTagError: %s, code: %s".formatted(name(), getCode());
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
        return ERROR_CONTENT_TAG;
    }
}
