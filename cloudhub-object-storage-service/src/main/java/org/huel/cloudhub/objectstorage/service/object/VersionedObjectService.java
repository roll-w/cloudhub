package org.huel.cloudhub.objectstorage.service.object;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.huel.cloudhub.objectstorage.data.dto.object.ObjectInfo;
import org.huel.cloudhub.objectstorage.data.dto.object.ObjectInfoDto;
import org.huel.cloudhub.objectstorage.data.entity.object.VersionedObject;

import java.util.List;

/**
 * @author RollW
 */
public interface VersionedObjectService {
    VersionedObject createNewVersionObject(ObjectInfoDto objectInfoDto);

    void deleteObjectVersion(ObjectInfo info, long version);

    @Nullable
    VersionedObject getObjectVersionOf(ObjectInfo info, long version);

    @Nullable
    VersionedObject getObjectVersionOf(String bucketName, String objectName, long version);

    List<VersionedObject> getObjectVersions(String bucketName, String objectName);

}
