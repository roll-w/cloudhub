package org.huel.cloudhub.client.disk.domain.usergroup.common;

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
public enum UserGroupErrorCode implements ErrorCode, ErrorCodeFinder, ErrorCodeMessageProvider {
    ERROR_USER_GROUP("B3600", 400),
    ERROR_GROUP_NOT_FOUND("B3601", 404),
    ERROR_GROUP_DELETED("B3602", 400),
    ERROR_GROUP_NAME_EXIST("B3603", 400),
    ERROR_GROUP_NAME_NOT_EXIST("B3604", 400),
    ERROR_GROUP_NAME_INVALID("B3605", 400),
    ERROR_GROUP_DESCRIPTION_INVALID("B3606", 400),
    ERROR_GROUP_SETTINGS_INVALID("B3607", 400),
    ERROR_GROUP_SETTINGS_KEY_INVALID("B3608", 400),
    ERROR_GROUP_SETTINGS_VALUE_INVALID("B3609", 400),

    ERROR_MEMBER_NOT_FOUND("B3610", 404),
    ERROR_MEMBER_EXIST("B3611", 400),

    ERROR_GROUP_MEMBER_ALREADY_EXISTS("B3612", 400),
    ERROR_GROUP_MEMBER_NOT_EXISTS("B3613", 400),;

    private final String value;
    private final int status;

    UserGroupErrorCode(String value, int status) {
        this.value = value;
        this.status = status;
    }

    @Override
    public String toString() {
        return "UserGroupError: %s, code: %s".formatted(name(), getCode());
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
        return ERROR_USER_GROUP;
    }
}
