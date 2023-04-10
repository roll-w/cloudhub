package org.huel.cloudhub.objectstorage.controller;

import org.huel.cloudhub.objectstorage.data.dto.user.UserInfo;
import org.huel.cloudhub.objectstorage.service.user.UserGetter;
import org.huel.cloudhub.web.BusinessRuntimeException;
import org.huel.cloudhub.web.AuthErrorCode;
import org.huel.cloudhub.web.UserErrorCode;

import javax.servlet.http.HttpServletRequest;

/**
 * @author RollW
 */
public class ValidateHelper {
    public static void validateUserAdmin(HttpServletRequest request,
                                         UserGetter userGetter) throws BusinessRuntimeException {
        UserInfo userInfo = userGetter.getCurrentUser(request);
        if (userInfo == null) {
            throw new BusinessRuntimeException(UserErrorCode.ERROR_USER_NOT_LOGIN);

        }
        if (!userInfo.role().hasPrivilege()) {
            throw new BusinessRuntimeException(AuthErrorCode.ERROR_NOT_HAS_ROLE);
        }
    }



    private ValidateHelper() {
    }
}
