package org.huel.cloudhub.web.data.dto;

/**
 * @author RollW
 */
public record UserPasswordDto(
        String username,
        String password,
        String email
) {
}
