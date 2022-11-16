package org.huel.cloudhub.client.service.user;

import org.huel.cloudhub.client.data.dto.user.UserInfo;
import org.huel.cloudhub.client.data.entity.user.Role;
import org.huel.cloudhub.client.data.entity.user.User;
import org.huel.cloudhub.common.MessagePackage;

import java.util.List;

/**
 * Admin user manage.
 *
 * @author RollW
 */
public interface UserManageService {
    default MessagePackage<UserInfo> createUser(String username, String password, String email, Role role) {
        return createUser(username, password, email, role, false);
    }

    default MessagePackage<UserInfo> createUser(String username, String password, String email) {
        return createUser(username, password, email, Role.USER);
    }

    default MessagePackage<UserInfo> createUser(String username, String password, String email, boolean discardEmail) {
        return createUser(username, password, email, Role.USER, discardEmail);
    }

    User queryUser(String username);

    /**
     * 创建用户。
     *
     * @param username     用户名
     * @param password     密码
     * @param email        邮箱地址
     * @param role         用户身份
     * @param discardEmail 是否忽略邮件重复，{@code true}忽略重复。
     * @return {@link MessagePackage}
     */
    MessagePackage<UserInfo> createUser(String username, String password,
                                        String email, Role role, boolean discardEmail);

    MessagePackage<Void> deleteUser(long userId);

    MessagePackage<Void> deleteUsers(List<Long> userId);

    /**
     * 修改用户身份
     */
    MessagePackage<UserInfo> setRoleTo(long userId, Role role);
}
