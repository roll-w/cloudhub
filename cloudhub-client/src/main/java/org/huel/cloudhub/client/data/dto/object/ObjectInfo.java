package org.huel.cloudhub.client.data.dto.object;

import org.huel.cloudhub.client.data.entity.object.FileObjectStorage;
import space.lingu.light.DataColumn;

/**
 * @author RollW
 */
public record ObjectInfo(
        @DataColumn(name = "object_name")
        String objectName,

        @DataColumn(name = "bucket_id")
        String bucketName
) {
    public static ObjectInfo from(FileObjectStorage storage) {
        return new ObjectInfo(
                storage.getObjectName(),
                storage.getBucketName());
    }
}
