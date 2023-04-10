package org.huel.cloudhub.objectstorage.data.dto.bucket;

/**
 * @author RollW
 */
public record BucketAdminDeleteRequest(
        Long userId,
        String bucketName
) {
}
