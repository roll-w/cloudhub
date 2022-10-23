package org.huel.cloudhub.web.data.dto;

/**
 * @author RollW
 */
public record AuthRequest(
        String username,
        String password) {
}
