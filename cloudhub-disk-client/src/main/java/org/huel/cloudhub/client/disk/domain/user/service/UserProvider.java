package org.huel.cloudhub.client.disk.domain.user.service;

import org.huel.cloudhub.client.disk.domain.user.AttributedUser;
import space.lingu.NonNull;

import java.util.List;

/**
 * @author RollW
 */
public interface UserProvider {
    /**
     * Search user by username/id.
     */
    AttributedUser tryFindUser(String username);

    AttributedUser findUser(long id);

    AttributedUser findUser(String username);

    /**
     * Search users by username/id/nickname/email.
     */
    List<AttributedUser> findUsers(@NonNull String keyword);
}
