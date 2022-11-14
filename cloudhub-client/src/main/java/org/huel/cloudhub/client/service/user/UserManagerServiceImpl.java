package org.huel.cloudhub.client.service.user;

import org.huel.cloudhub.client.data.dto.UserInfo;
import org.huel.cloudhub.client.data.entity.user.Role;
import org.huel.cloudhub.common.ErrorCode;
import org.huel.cloudhub.common.MessagePackage;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author RollW
 */
@Service
public class UserManagerServiceImpl implements UserManageService {
    // TODO:
    @Override
    public MessagePackage<UserInfo> createUser(String username, String password, String email, Role role, boolean discardEmail) {
        return null;
    }

    @Override
    public MessagePackage<Void> deleteUser(long userId) {
        return null;
    }

    @Override
    public MessagePackage<Void> deleteUsers(List<Long> userId) {
        return new MessagePackage<>(ErrorCode.SUCCESS, "", null);
    }

    @Override
    public MessagePackage<UserInfo> setRoleTo(long userId, Role role) {
        return null;
    }
}
