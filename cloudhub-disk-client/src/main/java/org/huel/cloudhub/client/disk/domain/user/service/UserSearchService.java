package org.huel.cloudhub.client.disk.domain.user.service;

import org.huel.cloudhub.client.disk.domain.user.AttributedUser;
import org.huel.cloudhub.client.disk.domain.user.UserIdentity;
import org.huel.cloudhub.client.disk.domain.user.common.UserViewException;

import java.util.List;

/**
 * Provides to user APIs, which are used to get user information.
 * Admin APIs should use {@link UserManageService}.
 *
 * @author RollW
 */
public interface UserSearchService {
    /**
     * Get user by id. And enables check if user is deleted or canceled.
     *
     * @throws UserViewException if user is deleted or canceled.
     */
    AttributedUser findUser(long userId) throws UserViewException;


    AttributedUser findUser(String username) throws UserViewException;

    List<? extends AttributedUser> findUsers(String username);

    /**
     * Get user by id. And enables check if user is deleted or canceled.
     *
     * @throws UserViewException if user is deleted or canceled.
     */
    AttributedUser findUser(UserIdentity userIdentity) throws UserViewException;

    List<? extends AttributedUser> findUsers(int page, int size);

    List<? extends AttributedUser> findUsers();

    List<? extends AttributedUser> findUsers(List<Long> ids);
}
