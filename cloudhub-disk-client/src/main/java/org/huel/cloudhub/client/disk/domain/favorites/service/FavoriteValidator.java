package org.huel.cloudhub.client.disk.domain.favorites.service;

import com.google.common.base.Strings;
import org.huel.cloudhub.client.disk.domain.favorites.common.FavoriteErrorCode;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResourceKind;
import org.huel.cloudhub.client.disk.domain.systembased.validate.FieldType;
import org.huel.cloudhub.client.disk.domain.systembased.validate.UnsupportedFieldException;
import org.huel.cloudhub.client.disk.domain.systembased.validate.Validator;
import org.huel.cloudhub.web.CommonErrorCode;
import org.huel.cloudhub.web.ErrorCode;
import org.springframework.stereotype.Component;
import space.lingu.NonNull;

/**
 * @author RollW
 */
@Component
public class FavoriteValidator implements Validator {
    @Override
    public boolean supports(SystemResourceKind systemResourceKind) {
        return systemResourceKind == SystemResourceKind.FAVORITE_GROUP;
    }

    private static final String[] PRESERVE_NAMES = {"default", "recycle-bin"};

    @NonNull
    @Override
    public ErrorCode validate(String value, @NonNull FieldType fieldType) {
        if (fieldType != FieldType.NAME) {
            throw new UnsupportedFieldException(fieldType);
        }
        if (Strings.isNullOrEmpty(value)) {
            return FavoriteErrorCode.ERROR_FAVORITE_NAME_NON_COMPLIANCE;
        }
        for (String preserveName : PRESERVE_NAMES) {
            if (preserveName.equals(value)) {
                return FavoriteErrorCode.ERROR_FAVORITE_NAME_NON_COMPLIANCE;
            }
        }
        if (value.length() > 20) {
            return FavoriteErrorCode.ERROR_FAVORITE_NAME_NON_COMPLIANCE;
        }
        return CommonErrorCode.SUCCESS;
    }
}
