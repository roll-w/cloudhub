package org.huel.cloudhub.client.disk.domain.user.service;

import org.huel.cloudhub.client.disk.domain.user.AttributedUser;

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
}
