package org.huel.cloudhub.objectstorage.data.dto.user;

/**
 * @author RollW
 */
public record UserPasswordResetRequest(
        String oldPassword,
        String newPassword) {
}
