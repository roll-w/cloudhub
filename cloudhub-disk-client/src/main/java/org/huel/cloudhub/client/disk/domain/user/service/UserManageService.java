package org.huel.cloudhub.client.disk.domain.user.service;


import org.huel.cloudhub.client.disk.domain.user.AttributedUser;
import org.huel.cloudhub.client.disk.domain.user.Role;
import org.huel.cloudhub.client.disk.domain.user.common.UserException;
import org.huel.cloudhub.web.data.page.Pageable;

import java.util.List;

/**
 * @author RollW
 */
public interface UserManageService {
    AttributedUser createUser(String username, String password,
                              String email, Role role, boolean enable);

    AttributedUser getUser(long userId) throws UserException;

    List<? extends AttributedUser> getUsers(Pageable pageable);

    List<? extends AttributedUser> getUsers();
}
