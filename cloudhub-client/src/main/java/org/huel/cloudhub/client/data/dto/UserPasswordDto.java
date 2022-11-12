package org.huel.cloudhub.client.data.dto;

/**
 * @author RollW
 */
public record UserPasswordDto(
        String username,
        String password,
        String email
) {
}
