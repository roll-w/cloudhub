package org.huel.cloudhub.client.data.dto.bucket;

/**
 * @author RollW
 */
public record BucketAdminDeleteRequest(
        Long userId,
        String bucketName
) {
}
