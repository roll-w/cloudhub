package org.huel.cloudhub.client.service.user;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.huel.cloudhub.client.data.dto.user.UserInfo;

import javax.servlet.http.HttpServletRequest;

/**
 * @author RollW
 */
public interface UserGetter {
    @Nullable
    UserInfo getCurrentUser(HttpServletRequest request);
}
