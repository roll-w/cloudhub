package org.huel.cloudhub.client.disk.domain.user.service;


import org.huel.cloudhub.client.disk.domain.user.AttributedUser;
import org.huel.cloudhub.client.disk.domain.user.Role;
import org.huel.cloudhub.client.disk.domain.user.common.UserViewException;
import org.huel.cloudhub.client.disk.domain.user.dto.UserInfo;
import org.huel.cloudhub.web.Result;

import java.util.List;

/**
 * @author RollW
 */
public interface UserManageService {
    Result<UserInfo> createUser(String username, String password,
                                String email, Role role, boolean enable);

    AttributedUser getUser(long userId) throws UserViewException;

    List<? extends AttributedUser> getUsers(int page, int size);

    List<? extends AttributedUser> getUsers();

    void deleteUser(long userId);

    void setUserEnable(long userId, boolean enable);
}
