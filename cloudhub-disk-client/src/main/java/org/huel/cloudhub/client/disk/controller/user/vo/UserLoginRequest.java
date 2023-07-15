package org.huel.cloudhub.client.disk.controller.user.vo;

/**
 * @param identity Identity, could be a username or email, etc.
 * @param token    login token, could be the password or a code.
 * @author RollW
 */
public record UserLoginRequest(
        String identity,
        String token
) {
}
