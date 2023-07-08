package org.huel.cloudhub.client.disk.domain.user.service;

import org.huel.cloudhub.client.disk.domain.user.User;
import org.huel.cloudhub.client.disk.domain.user.filter.UserInfoFilter;

/**
 * @author RollW
 */
public interface UserOperatorDelegate {
    void updateUser(User user);

    boolean checkUsernameExist(String username);

    boolean checkEmailExist(String email);

    boolean validatePassword(User user, String password);

    UserInfoFilter getUserInfoFilter();

    String encodePassword(String password);
}
