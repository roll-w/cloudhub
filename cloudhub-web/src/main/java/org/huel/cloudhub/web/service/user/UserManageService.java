package org.huel.cloudhub.web.service.user;

import org.huel.cloudhub.web.data.entity.user.Role;

import java.util.List;

/**
 * Admin user manage.
 *
 * @author RollW
 */
public interface UserManageService {
    // TODO: 需要实现。

    default void createUser(String username, String password, String email, Role role) {
        createUser(username, password, email, role, false);
    }

    default void createUser(String username, String password, String email) {
        createUser(username, password, email, Role.USER);
    }

    default void createUser(String username, String password, String email, boolean discardEmail) {
        createUser(username, password, email, Role.USER, discardEmail);
    }

    void createUser(String username, String password, String email, Role role, boolean discardEmail);

    void deleteUser(long userId);

    void deleteUsers(List<Long> userId);

    void setRoleTo(long userId, Role role);
}
