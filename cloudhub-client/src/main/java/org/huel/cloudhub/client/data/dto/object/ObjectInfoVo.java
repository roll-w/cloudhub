package org.huel.cloudhub.client.data.dto.object;

import org.huel.cloudhub.client.data.entity.object.FileObjectStorage;
import space.lingu.light.DataColumn;

/**
 * @author RollW
 */
public record ObjectInfoVo(
        @DataColumn(name = "object_name")
        String objectName,

        @DataColumn(name = "bucket_name")
        String bucketName,

        @DataColumn(name = "object_create_time")
        long createTime,

        @DataColumn(name = "object_size")
        long objectSize) {

    public static ObjectInfoVo from(FileObjectStorage storage) {
        if (storage == null) {
            return null;
        }
        return new ObjectInfoVo(
                storage.getObjectName(),
                storage.getBucketName(),
                storage.getCreateTime(),
                storage.getObjectSize());
    }

    public static ObjectInfoVo from(ObjectInfoDto objectInfoDto) {
        if (objectInfoDto == null) {
            return null;
        }
        return new ObjectInfoVo(
                objectInfoDto.objectName(),
                objectInfoDto.bucketName(),
                objectInfoDto.createTime(),
                objectInfoDto.objectSize());
    }
}