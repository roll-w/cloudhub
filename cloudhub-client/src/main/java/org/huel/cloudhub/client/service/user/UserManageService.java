package org.huel.cloudhub.client.service.user;

import org.huel.cloudhub.client.data.dto.UserInfo;
import org.huel.cloudhub.client.data.entity.user.Role;
import org.huel.cloudhub.common.MessagePackage;

import java.util.List;

/**
 * Admin user manage.
 *
 * @author RollW
 */
public interface UserManageService {
    // TODO: 需要实现。

    default MessagePackage<UserInfo> createUser(String username, String password, String email, Role role) {
        return createUser(username, password, email, role, false);
    }

    default MessagePackage<UserInfo> createUser(String username, String password, String email) {
        return createUser(username, password, email, Role.USER);
    }

    default MessagePackage<UserInfo> createUser(String username, String password, String email, boolean discardEmail) {
        return createUser(username, password, email, Role.USER, discardEmail);
    }

    MessagePackage<UserInfo> createUser(String username, String password, String email, Role role, boolean discardEmail);

    MessagePackage<Void> deleteUser(long userId);

    MessagePackage<Void> deleteUsers(List<Long> userId);

    MessagePackage<UserInfo> setRoleTo(long userId, Role role);
}
