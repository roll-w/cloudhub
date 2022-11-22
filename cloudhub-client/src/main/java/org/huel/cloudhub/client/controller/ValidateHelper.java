package org.huel.cloudhub.client.controller;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.huel.cloudhub.client.data.dto.user.UserInfo;
import org.huel.cloudhub.client.service.user.UserGetter;
import org.huel.cloudhub.common.ErrorCode;
import org.huel.cloudhub.common.MessagePackage;

import javax.servlet.http.HttpServletRequest;

/**
 * @author RollW
 */
public class ValidateHelper {
    @Nullable
    public static MessagePackage<?> validateUserAdmin(HttpServletRequest request, UserGetter userGetter) {
        UserInfo userInfo = userGetter.getCurrentUser(request);
        if (userInfo == null) {
            return new MessagePackage<>(ErrorCode.ERROR_USER_NOT_LOGIN,
                    "User not login.");
        }
        if (!userInfo.role().hasPrivilege()) {
            return new MessagePackage<>(ErrorCode.ERROR_PERMISSION_NOT_ALLOWED,
                    "User has no permissions.");
        }
        return null;
    }

    private ValidateHelper() {
    }
}
