package org.huel.cloudhub.client.disk.domain.user.vo;

import org.huel.cloudhub.client.disk.domain.user.AttributedUser;
import org.huel.cloudhub.client.disk.domain.user.Role;
import org.huel.cloudhub.client.disk.domain.user.UserIdentity;

/**
 * @author RollW
 */
public record UserDetailsVo(
        long userId,
        Role role,
        String username,
        String email,
        boolean enabled,
        boolean locked,
        boolean canceled,
        long createdAt,
        long updatedAt,
        String nickname
) {

    public static UserDetailsVo of(UserIdentity userIdentity) {
        if (userIdentity instanceof AttributedUser user) {
            return of(user);
        }
        return null;
    }

    public static UserDetailsVo of(AttributedUser user) {
        return new UserDetailsVo(
                user.getUserId(),
                user.getRole(),
                user.getUsername(),
                user.getEmail(),
                user.isEnabled(),
                user.isLocked(),
                user.isCanceled(),
                user.getCreateTime(),
                user.getUpdateTime(),
                user.getNickname()
        );
    }
}
