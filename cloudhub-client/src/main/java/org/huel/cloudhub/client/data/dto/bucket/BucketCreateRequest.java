package org.huel.cloudhub.client.data.dto.bucket;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.huel.cloudhub.client.data.entity.bucket.BucketVisibility;

/**
 * @author Cheng
 */
public record BucketCreateRequest(

        @NonNull
        String bucketName,

        @Nullable
        long userId,

        @Nullable
        BucketVisibility visibility



) {
}
