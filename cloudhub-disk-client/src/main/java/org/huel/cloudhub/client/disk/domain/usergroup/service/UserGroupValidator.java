package org.huel.cloudhub.client.disk.domain.usergroup.service;

import com.google.common.base.Strings;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResourceKind;
import org.huel.cloudhub.client.disk.domain.systembased.validate.FieldType;
import org.huel.cloudhub.client.disk.domain.systembased.validate.UnsupportedFieldException;
import org.huel.cloudhub.client.disk.domain.systembased.validate.Validator;
import org.huel.cloudhub.client.disk.domain.usergroup.common.UserGroupErrorCode;
import org.huel.cloudhub.client.disk.domain.usergroup.common.UserGroupException;
import org.huel.cloudhub.web.BusinessRuntimeException;
import org.huel.cloudhub.web.CommonErrorCode;
import org.huel.cloudhub.web.ErrorCode;
import org.springframework.stereotype.Component;
import space.lingu.NonNull;

/**
 * @author RollW
 */
@Component
public class UserGroupValidator implements Validator {
    @Override
    public boolean supports(SystemResourceKind systemResourceKind) {
        return systemResourceKind == SystemResourceKind.USER_GROUP;
    }

    @NonNull
    @Override
    public ErrorCode validate(String value, @NonNull FieldType fieldType) {
        return switch (fieldType) {
            case NAME -> {
                if (Strings.isNullOrEmpty(value) || value.length() < 2 || value.length() > 32) {
                    yield UserGroupErrorCode.ERROR_GROUP_NAME_INVALID;
                }
                if (value.equals("default")) {
                    yield UserGroupErrorCode.ERROR_GROUP_NAME_EXIST;
                }

                yield CommonErrorCode.SUCCESS;
            }
            case DESCRIPTION -> {
                if (Strings.isNullOrEmpty(value)) {
                    yield CommonErrorCode.SUCCESS;
                }
                if (value.length() > 100) {
                    yield UserGroupErrorCode.ERROR_GROUP_DESCRIPTION_INVALID;
                }
                yield CommonErrorCode.SUCCESS;
            }
            default -> throw new UnsupportedFieldException(fieldType);
        };
    }

    @Override
    public void validateThrows(String value, @NonNull FieldType fieldType)
            throws BusinessRuntimeException {
        ErrorCode errorCode = validate(value, fieldType);
        if (errorCode.failed()) {
            throw new UserGroupException(errorCode);
        }
    }
}
