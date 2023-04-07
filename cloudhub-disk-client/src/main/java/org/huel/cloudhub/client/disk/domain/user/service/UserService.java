package org.huel.cloudhub.client.disk.domain.user.service;


import org.huel.cloudhub.client.disk.domain.user.Role;
import org.huel.cloudhub.client.disk.domain.user.dto.UserInfo;
import org.huel.cloudhub.web.Result;

/**
 * @author RollW
 */
public interface UserService {
    Result<UserInfo> createUser(String username, String password,
                                String email, Role role);

    void deleteUser(long userId);
}
