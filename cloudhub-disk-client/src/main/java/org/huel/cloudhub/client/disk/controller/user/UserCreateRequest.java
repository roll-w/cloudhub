package org.huel.cloudhub.client.disk.controller.user;

import org.huel.cloudhub.client.disk.domain.user.Role;

/**
 * @author RollW
 */
public record UserCreateRequest(
        String username,
        String password,
        String email,
        Role role
) {
}
