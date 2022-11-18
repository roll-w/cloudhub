package org.huel.cloudhub.client.data.dto.bucket;

import space.lingu.light.DataColumn;

/**
 * @author Cheng
 */
public record BucketInfo(

        @DataColumn(name = "bucket_name")
        String name,

        @DataColumn(name = "bucket_user_id")
         Long userId

) {
}
