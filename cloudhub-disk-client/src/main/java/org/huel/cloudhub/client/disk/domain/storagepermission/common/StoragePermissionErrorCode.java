package org.huel.cloudhub.client.disk.domain.storagepermission.common;

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
public enum StoragePermissionErrorCode implements ErrorCode, ErrorCodeFinder, ErrorCodeMessageProvider {
    ERROR_STORAGE_PERMISSION("B3300", 400),

    ERROR_PERMISSION_NOT_FOUND("B3301", 404),
    ERROR_PERMISSION_ALREADY_EXIST("B3302", 400),
    ERROR_PERMISSION_NOT_ALLOWED("B3303", 403),
    ERROR_PERMISSION_NOT_ALLOWED_TO_UPDATE("B3305", 403),
    ERROR_PERMISSION_NOT_ALLOWED_TO_CREATE("B3306", 403),
    ERROR_PERMISSION_NOT_ALLOWED_TO_READ("B3307", 403),
    ERROR_PERMISSION_NOT_ALLOWED_TO_WRITE("B3308", 403),

    ERROR_PERMISSION_ASSIGN_NOT_ALLOWED("B3315", 403),
    ERROR_PERMISSION_NOT_ALLOW_USER("B3316", 403),

    ;

    private final String value;
    private final int status;

    StoragePermissionErrorCode(String value, int status) {
        this.value = value;
        this.status = status;
    }

    @Override
    public String toString() {
        return "StoragePermissionError: %s, code: %s".formatted(name(), getCode());
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
        return ERROR_STORAGE_PERMISSION;
    }
}
