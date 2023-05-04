package org.huel.cloudhub.client.disk.domain.share.dto;

import org.huel.cloudhub.client.disk.domain.userstorage.StorageType;

import java.time.Duration;

/**
 * @author RollW
 */
public record ShareInfo(
        long id,
        long storageId,
        StorageType storageType,
        long creatorId,
        String shareCode,
        Duration time,
        long createTime
) {
}
