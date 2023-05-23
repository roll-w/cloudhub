package org.huel.cloudhub.client.disk.domain.user.service;

import org.huel.cloudhub.client.disk.domain.user.AttributedUser;
import org.huel.cloudhub.client.disk.domain.user.UserIdentity;
import org.huel.cloudhub.client.disk.domain.user.common.UserException;
import org.huel.cloudhub.web.data.page.Pageable;

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
     * @throws UserException if user is deleted or canceled.
     */
    AttributedUser findUser(long userId) throws UserException;


    AttributedUser findUser(String username) throws UserException;

    List<? extends AttributedUser> findUsers(String username);

    /**
     * Get user by id. And enables check if user is deleted or canceled.
     *
     * @throws UserException if user is deleted or canceled.
     */
    AttributedUser findUser(UserIdentity userIdentity) throws UserException;

    List<? extends AttributedUser> findUsers(Pageable pageable);

    List<? extends AttributedUser> findUsers();

    List<? extends AttributedUser> findUsers(List<Long> ids);

    static AttributedUser binarySearch(long id, List<? extends AttributedUser> attributedUsers) {
        int low = 0;
        int high = attributedUsers.size() - 1;
        while (low <= high) {
            int mid = (low + high) >>> 1;
            AttributedUser attributedUser = attributedUsers.get(mid);
            if (attributedUser.getUserId() < id) {
                low = mid + 1;
            } else if (attributedUser.getUserId() > id) {
                high = mid - 1;
            } else {
                return attributedUser;
            }
        }
        return null;
    }
}
