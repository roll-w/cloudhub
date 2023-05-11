package org.huel.cloudhub.client.disk.domain.user.dto;

import org.huel.cloudhub.client.disk.domain.user.User;
import org.huel.cloudhub.web.RequestMetadata;

/**
 * @author RollW
 */
public record LoginLog(
        long id,
        long userId,
        String username,
        String ip,
        long timestamp,
        String userAgent,
        boolean success
) {
    public static LoginLog from(User user, RequestMetadata metadata, boolean success) {
        return new LoginLog(
                0,
                user.getId(),
                user.getUsername(),
                metadata.ip(),
                metadata.timestamp(),
                metadata.userAgent(),
                success
        );
    }
}
