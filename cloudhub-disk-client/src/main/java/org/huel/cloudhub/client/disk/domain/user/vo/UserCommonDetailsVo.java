package org.huel.cloudhub.client.disk.domain.user.vo;


import org.huel.cloudhub.client.disk.domain.user.Role;
import org.huel.cloudhub.client.disk.domain.user.UserIdentity;

/**
 * When the user is logged in, the user's personal information is displayed on the page.
 *
 * @author RollW
 */
public record UserCommonDetailsVo(
        long userId,
        Role role,
        String username,
        String email
) {
    // TODO: add followers/following count

    public static UserCommonDetailsVo of(UserIdentity userIdentity) {
        return new UserCommonDetailsVo(
                userIdentity.getUserId(),
                userIdentity.getRole(),
                userIdentity.getUsername(),
                userIdentity.getEmail()
        );
    }
}
