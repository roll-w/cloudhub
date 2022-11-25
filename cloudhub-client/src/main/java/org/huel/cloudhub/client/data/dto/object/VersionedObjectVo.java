package org.huel.cloudhub.client.data.dto.object;

import org.huel.cloudhub.client.data.entity.object.VersionedObject;
import space.lingu.light.DataColumn;

/**
 * @author RollW
 */
public record VersionedObjectVo(
        @DataColumn(name = "object_name")
        String objectName,

        @DataColumn(name = "bucket_name")
        String bucketName,

        @DataColumn(name = "object_version")
        Long version,

        @DataColumn(name = "last_modified")
        Long lastModified
) {
    public static VersionedObjectVo from(VersionedObject object) {
        if (object.getVersion() < 0) {
            return new VersionedObjectVo(
                    object.getObjectName(),
                    object.getBucketName(),
                    null, object.getLastModified());
        }
        return new VersionedObjectVo(
                object.getObjectName(),
                object.getBucketName(),
                object.getVersion(), object.getLastModified());
    }
}
