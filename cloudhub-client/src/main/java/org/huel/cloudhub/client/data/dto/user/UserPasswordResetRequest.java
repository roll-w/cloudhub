package org.huel.cloudhub.client.data.dto.user;

/**
 * @author RollW
 */
public record UserPasswordResetRequest(
        String oldPassword,
        String newPassword) {
}
