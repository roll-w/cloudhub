package org.huel.cloudhub.objectstorage.data.dto.bucket;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.huel.cloudhub.objectstorage.data.entity.bucket.BucketVisibility;

/**
 * @author Cheng
 */
public record BucketCreateRequest(
        @NonNull
        String bucketName,

        @Nullable
        BucketVisibility visibility
) {
}
