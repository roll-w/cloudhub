package org.huel.cloudhub.web.service.user;

import org.huel.cloudhub.web.data.entity.user.Role;

import java.util.List;

/**
 * Admin
 *
 * @author RollW
 */
public interface UserManageService {
    // TODO: 需要实现。

    void createUser(String username, String password, String email, Role role);

    void createUser(String username, String password, String email, Role role, boolean discardEmail);

    void createUser(String username, String password, String email);

    void createUser(String username, String password, String email, boolean discardEmail);

    void deleteUser(long userId);

    void deleteUser(List<Long> userId);

    void setRoleTo(long userId, Role role);

}
