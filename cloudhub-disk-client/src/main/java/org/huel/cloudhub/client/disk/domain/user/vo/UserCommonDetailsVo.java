package org.huel.cloudhub.client.disk.domain.user.vo;


import org.huel.cloudhub.client.disk.domain.user.AttributedUser;
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
        String nickname,
        String email
) {

    public static UserCommonDetailsVo of(AttributedUser attributedUser) {
        if (attributedUser == null) {
            return null;
        }
        String nickname = attributedUser.getNickname() == null
                ? attributedUser.getUsername()
                : attributedUser.getNickname();

        return new UserCommonDetailsVo(
                attributedUser.getUserId(),
                attributedUser.getRole(),
                attributedUser.getUsername(),
                nickname,
                attributedUser.getEmail()
        );
    }

    public static UserCommonDetailsVo of(UserIdentity userIdentity) {


        return new UserCommonDetailsVo(
                userIdentity.getUserId(),
                userIdentity.getRole(),
                userIdentity.getUsername(),
                null,
                userIdentity.getEmail()
        );
    }
}
