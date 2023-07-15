package org.huel.cloudhub.client.disk.controller.user;

import org.huel.cloudhub.client.disk.domain.user.Role;

/**
 * @author RollW
 */
public record UserUpdateRequest(
        String nickname,
        String email,
        Role role,
        boolean enabled,
        boolean locked,
        boolean canceled
) {
}
