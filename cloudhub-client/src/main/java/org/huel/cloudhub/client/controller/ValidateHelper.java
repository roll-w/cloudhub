package org.huel.cloudhub.client.controller;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.huel.cloudhub.client.data.dto.user.UserInfo;
import org.huel.cloudhub.client.service.user.UserGetter;
import org.huel.cloudhub.common.ErrorCode;
import org.huel.cloudhub.common.HttpResponseEntity;

import javax.servlet.http.HttpServletRequest;

/**
 * @author RollW
 */
public class ValidateHelper {
    @Nullable
    public static HttpResponseEntity<?> getHttpResponseEntity(HttpServletRequest request, UserGetter userGetter) {
        UserInfo userInfo = userGetter.getCurrentUser(request);
        if (userInfo == null) {
            return HttpResponseEntity.failure("User not login.",
                    ErrorCode.ERROR_USER_NOT_LOGIN);
        }
        if (!userInfo.role().hasPrivilege()) {
            return HttpResponseEntity.failure("User has no permissions.",
                    ErrorCode.ERROR_PERMISSION_NOT_ALLOWED);
        }
        return null;
    }

    private ValidateHelper() {
    }
}
