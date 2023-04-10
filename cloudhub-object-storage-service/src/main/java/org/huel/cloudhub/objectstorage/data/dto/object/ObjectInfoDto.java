package org.huel.cloudhub.objectstorage.data.dto.object;

import org.huel.cloudhub.objectstorage.data.entity.object.FileObjectStorage;
import space.lingu.light.DataColumn;

/**
 * @author RollW
 */
public record ObjectInfoDto(
        @DataColumn(name = "object_name")
        String objectName,

        @DataColumn(name = "bucket_name")
        String bucketName,

        @DataColumn(name = "file_id")
        String fileId,

        @DataColumn(name = "object_create_time")
        long createTime,

        @DataColumn(name = "object_size")
        long objectSize) {

    public static ObjectInfoDto from(FileObjectStorage storage) {
        if (storage == null) {
            return null;
        }
        return new ObjectInfoDto(
                storage.getObjectName(),
                storage.getBucketName(),
                storage.getFileId(),
                storage.getCreateTime(),
                storage.getObjectSize());
    }
}
