package org.huel.cloudhub.objectstorage.service.user;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.huel.cloudhub.objectstorage.data.dto.user.UserInfo;

import javax.servlet.http.HttpServletRequest;

/**
 * @author RollW
 */
public interface UserGetter {
    @Nullable
    UserInfo getCurrentUser(HttpServletRequest request);
}
