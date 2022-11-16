package org.huel.cloudhub.client.service.user;

import org.huel.cloudhub.client.data.database.repository.UserRepository;
import org.huel.cloudhub.client.data.dto.UserInfo;
import org.huel.cloudhub.client.data.entity.user.Role;
import org.huel.cloudhub.client.data.entity.user.User;
import org.huel.cloudhub.common.ErrorCode;
import org.huel.cloudhub.common.MessagePackage;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;

/**
 * @author RollW
 */
@Service
public class UserManagerServiceImpl implements UserManageService {

    private UserRepository userRepository;

    public UserManagerServiceImpl (UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public User queryUser(String username) {
        User user = userRepository.getUserByName(username);
        return user;
    }

    // TODO:
    @Override
    public MessagePackage<UserInfo> createUser(String username, String password, String email, Role role, boolean discardEmail) {
        User user = new User(username,password,Role.USER,new Date().getTime(),email);
        userRepository.save(user);
        return  new MessagePackage<UserInfo>(ErrorCode.SUCCESS, "", null);
    }

    @Override
    public MessagePackage<Void> deleteUser(long userId) {
        if (userRepository.isExistById(userId)){
            userRepository.delete(userId);
            return new MessagePackage<>(ErrorCode.SUCCESS, "", null);
        }else {
            return new MessagePackage<>(ErrorCode.ERROR_USER_NOT_EXIST, "", null);
        }
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
