package org.huel.cloudhub.client.disk.controller.user;

/**
 * @author RollW
 */
public record UserRegisterRequest(
        String username,
        String password,
        String email) {
}
