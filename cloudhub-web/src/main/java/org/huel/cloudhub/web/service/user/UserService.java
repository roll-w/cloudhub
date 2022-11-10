package org.huel.cloudhub.web.service.user;

import org.huel.cloudhub.common.MessagePackage;
import org.huel.cloudhub.web.data.dto.UserInfo;
import org.huel.cloudhub.web.data.entity.user.Role;

import javax.servlet.http.HttpServletRequest;

/**
 * @author RollW
 */
public interface UserService extends UserVerifyService {

    MessagePackage<UserInfo> registerUser(String username, String password, String email);

    MessagePackage<UserInfo> registerUser(String username, String password,
                                          String email, Role role);

    MessagePackage<UserInfo> loginByUsername(HttpServletRequest request, String username, String password);

    UserInfo getCurrentUser(HttpServletRequest request);

    void logout(HttpServletRequest request);
}
