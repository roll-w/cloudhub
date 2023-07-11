package org.huel.cloudhub.client.disk.domain.tag.service;

import com.google.common.base.Strings;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResourceKind;
import org.huel.cloudhub.client.disk.domain.systembased.validate.FieldType;
import org.huel.cloudhub.client.disk.domain.systembased.validate.UnsupportedFieldException;
import org.huel.cloudhub.client.disk.domain.systembased.validate.Validator;
import org.huel.cloudhub.client.disk.domain.tag.common.ContentTagErrorCode;
import org.huel.cloudhub.web.CommonErrorCode;
import org.huel.cloudhub.web.ErrorCode;
import org.huel.cloudhub.web.WebCommonErrorCode;
import org.springframework.stereotype.Component;
import space.lingu.NonNull;

/**
 * @author RollW
 */
@Component
public class ContentTagValidator implements Validator {
    @Override
    public boolean supports(SystemResourceKind systemResourceKind) {
        return systemResourceKind == SystemResourceKind.TAG;
    }

    @NonNull
    @Override
    public ErrorCode validate(String value, @NonNull FieldType fieldType) {
        return  switch (fieldType) {
            case NAME -> validateName(value);
            case DESCRIPTION -> validateDescription(value);
            default -> throw new UnsupportedFieldException(fieldType);
        };
    }

    private ErrorCode validateName(String name) {
        if(Strings.isNullOrEmpty(name)) {
            return WebCommonErrorCode.ERROR_PARAM_MISSING;
        }
        if(name.length() > 20 || name.length() < 2) {
            return ContentTagErrorCode.ERROR_NAME_INVALID;
        }
        return CommonErrorCode.SUCCESS;
    }

    private ErrorCode validateDescription(String description) {
        if(Strings.isNullOrEmpty(description)) {
            return WebCommonErrorCode.ERROR_PARAM_MISSING;
        }
        if(description.length() > 100) {
            return ContentTagErrorCode.ERROR_DESCRIPTION_TOO_LONG;
        }
        return CommonErrorCode.SUCCESS;
    }
}
