package org.huel.cloudhub.client.disk.controller.usergroup.vo;

import org.huel.cloudhub.client.disk.domain.user.AttributedUser;
import org.huel.cloudhub.client.disk.domain.user.LegalUserType;

/**
 * @author RollW
 */
public record UserGroupMemberVo(
        LegalUserType type,
        long userId,
        String name
) {

    public static UserGroupMemberVo from(AttributedUser user)  {
        return new UserGroupMemberVo(
                LegalUserType.USER,
                user.getUserId(),
                user.getUsername()
        );
    }
}
