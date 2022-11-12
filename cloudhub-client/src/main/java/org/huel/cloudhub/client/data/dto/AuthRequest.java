package org.huel.cloudhub.client.data.dto;

/**
 * @author RollW
 */
public record AuthRequest(
        String username,
        String password) {
}
