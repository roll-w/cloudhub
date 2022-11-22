package org.huel.cloudhub.client.data.dto.object;

import org.huel.cloudhub.client.data.entity.object.FileObjectStorage;
import space.lingu.light.DataColumn;

/**
 * @author RollW
 */
public record ObjectInfoDto(
        @DataColumn(name = "object_name")
        String objectName,

        @DataColumn(name = "bucket_id")
        String bucketName,

        @DataColumn(name = "file_id")
        String fileId,

        @DataColumn(name = "object_create_time")
        long createTime,

        @DataColumn(name = "object_size")
        long objectSize) {

    public static ObjectInfoDto from(FileObjectStorage storage) {
        return new ObjectInfoDto(
                storage.getObjectName(),
                storage.getBucketName(),
                storage.getFileId(),
                storage.getCreateTime(),
                storage.getObjectSize());
    }
}
