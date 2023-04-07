package org.huel.cloudhub.objectstorage.data.dto.object;

import org.huel.cloudhub.objectstorage.data.entity.object.FileObjectStorage;
import space.lingu.light.DataColumn;

/**
 * @author RollW
 */
public record ObjectInfo(
        @DataColumn(name = "object_name")
        String objectName,

        @DataColumn(name = "bucket_name")
        String bucketName
) {
    public static ObjectInfo from(FileObjectStorage storage) {
        return new ObjectInfo(
                storage.getObjectName(),
                storage.getBucketName());
    }
}
