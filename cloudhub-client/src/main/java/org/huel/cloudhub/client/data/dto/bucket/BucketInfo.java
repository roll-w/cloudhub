package org.huel.cloudhub.client.data.dto.bucket;

import org.huel.cloudhub.client.data.entity.bucket.BucketVisibility;
import space.lingu.light.DataColumn;

/**
 * @author Cheng
 */
public record BucketInfo(
        @DataColumn(name = "bucket_name")
        String name,

        @DataColumn(name = "bucket_user_id")
        Long userId,

        @DataColumn(name = "bucket_create_time")
        long createTime,

        @DataColumn(name = "bucket_visibility")
        BucketVisibility bucketVisibility
) {
}
