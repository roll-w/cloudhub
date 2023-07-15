package org.huel.cloudhub.client.disk.controller.usergroup.vo;

import org.huel.cloudhub.client.disk.domain.user.LegalUserType;

/**
 * @author RollW
 */
public record UserGroupMemberCreateRequest(
        String name,
        LegalUserType type
) {
}
