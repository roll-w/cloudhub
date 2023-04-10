package org.huel.cloudhub.objectstorage.data.dto;

/**
 * @author RollW
 */
public record AuthRequest(
        String username,
        String password) {
}
