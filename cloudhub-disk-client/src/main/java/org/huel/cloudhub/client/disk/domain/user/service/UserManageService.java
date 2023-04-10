package org.huel.cloudhub.client.disk.domain.user.service;


import org.huel.cloudhub.client.disk.domain.user.Role;
import org.huel.cloudhub.client.disk.domain.user.User;
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

    User getUser(long userId) throws UserViewException;

    List<User> getUsers(int page, int size);

    List<User> getUsers();

    void deleteUser(long userId);

    void setUserEnable(long userId, boolean enable);

    void setBlockUser(long userId, boolean block);
}
