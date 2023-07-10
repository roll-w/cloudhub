package org.huel.cloudhub.client.disk.domain.systembased.validate;

import org.huel.cloudhub.client.disk.domain.systembased.SystemResourceSupportable;
import org.huel.cloudhub.web.BusinessRuntimeException;
import org.huel.cloudhub.web.ErrorCode;
import space.lingu.NonNull;

/**
 * @author RollW
 */
public interface Validator extends SystemResourceSupportable {
    default void validateThrows(String value, @NonNull FieldType fieldType)
            throws BusinessRuntimeException {
        ErrorCode errorCode = validate(value, fieldType);
        if (errorCode.failed()) {
            throw new BusinessRuntimeException(errorCode);
        }
    }

    @NonNull
    ErrorCode validate(String value, @NonNull FieldType fieldType);

}
