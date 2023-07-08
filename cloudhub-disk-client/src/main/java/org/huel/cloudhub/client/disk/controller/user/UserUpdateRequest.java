package org.huel.cloudhub.client.disk.controller.user;

/**
 * @author RollW
 */
public record UserUpdateRequest(
        String nickname,
        String email
) {
}
