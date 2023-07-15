package org.huel.cloudhub.client.disk.controller.user.vo;

/**
 * @author RollW
 */
public record AdminUserUpdateRequest(
        String username,
        String password,
        String email,
        String nickname
) {
}
