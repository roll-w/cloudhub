package org.huel.cloudhub.client.disk.domain.user.service;

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
    UserIdentity findUser(long userId) throws UserViewException;


    UserIdentity findUser(String username) throws UserViewException;

    List<? extends UserIdentity> findUsers(String username);

    /**
     * Get user by id. And enables check if user is deleted or canceled.
     *
     * @throws UserViewException if user is deleted or canceled.
     */
    UserIdentity findUser(UserIdentity userIdentity) throws UserViewException;

    List<? extends UserIdentity> findUsers(int page, int size);

    List<? extends UserIdentity> findUsers();

    List<? extends UserIdentity> findUsers(List<Long> ids);
}
