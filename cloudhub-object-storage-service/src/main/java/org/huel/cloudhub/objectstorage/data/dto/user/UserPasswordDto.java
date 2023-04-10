package org.huel.cloudhub.objectstorage.data.dto.user;

/**
 * @author RollW
 */
public record UserPasswordDto(
        String username,
        String password,
        String email
) {
}
