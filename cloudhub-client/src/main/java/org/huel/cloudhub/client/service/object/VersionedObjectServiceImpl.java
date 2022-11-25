package org.huel.cloudhub.client.service.object;

import org.apache.commons.lang3.Validate;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.huel.cloudhub.client.data.database.repository.VersionedObjectRepository;
import org.huel.cloudhub.client.data.dto.object.ObjectInfo;
import org.huel.cloudhub.client.data.dto.object.ObjectInfoDto;
import org.huel.cloudhub.client.data.entity.object.VersionedObject;
import org.huel.cloudhub.common.ErrorCode;
import org.huel.cloudhub.common.MessagePackage;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author RollW
 */
@Service
public class VersionedObjectServiceImpl implements VersionedObjectService,
        ObjectRemoveHandler, ObjectChangeActionHandler {
    private final VersionedObjectRepository versionedObjectRepository;

    public VersionedObjectServiceImpl(VersionedObjectRepository versionedObjectRepository) {
        this.versionedObjectRepository = versionedObjectRepository;
    }

    @Override
    public MessagePackage<VersionedObject> createNewVersionObject(ObjectInfoDto objectInfoDto) {
        validateObjectInfoDto(objectInfoDto);

        VersionedObject latest =
                versionedObjectRepository.getLatestObject(objectInfoDto.bucketName(), objectInfoDto.objectName());
        long version = 1;
        if (latest != null) {
            version = latest.getVersion() + 1;
        }
        VersionedObject versionedObject = new VersionedObject(
                objectInfoDto.bucketName(),
                objectInfoDto.objectName(),
                objectInfoDto.fileId(),
                version,
                objectInfoDto.createTime());
        versionedObjectRepository.insert(versionedObject);
        return new MessagePackage<>(ErrorCode.SUCCESS, versionedObject);
    }

    @Override
    public void deleteObjectVersion(ObjectInfo info, long version) {
        validateObjectInfo(info);
        if (version <= 0) {
            return;
        }
        versionedObjectRepository.deleteVersion(info.bucketName(), info.objectName(), version);
    }

    public void deleteObjects(String bucketName, String objectName) {
        versionedObjectRepository.deleteObjects(bucketName, objectName);
    }


    public void deleteObjects(String bucketName) {
        Validate.notNull(bucketName, "bucketName cannot be null");
        versionedObjectRepository.deleteObjects(bucketName);
    }

    @Override
    @Nullable
    public VersionedObject getObjectVersionOf(ObjectInfo info, long version) {
        validateObjectInfo(info);
        return versionedObjectRepository.getVersionedObject(
                info.bucketName(),
                info.objectName(), version);
    }

    @Override
    public @Nullable VersionedObject getObjectVersionOf(String bucketName, String objectName, long version) {
        Validate.notEmpty(objectName, "objectName cannot be null");
        Validate.notEmpty(bucketName, "bucketName cannot be null");
        return versionedObjectRepository.getVersionedObject(
                bucketName,
                objectName, version);
    }

    @Override
    public List<VersionedObject> getObjectVersions(String bucketName, String objectName) {
        Validate.notNull(bucketName, "bucketName cannot be null");
        Validate.notNull(objectName, "objectName cannot be null");
        List<VersionedObject> objects = versionedObjectRepository.getVersionedObjects(
                bucketName, objectName);
        if (objects == null) {
            return List.of();
        }
        return objects;
    }

    @Override
    public void handleObjectRemove(ObjectInfoDto objectInfoDto) {
        validateObjectInfoDto(objectInfoDto);
        deleteObjects(objectInfoDto.bucketName(), objectInfoDto.objectName());
    }

    @Override
    public void handleObjectRemove(List<ObjectInfoDto> objectInfoDtos) {
        objectInfoDtos.forEach(this::handleObjectRemove);
    }

    @Override
    public void handleBucketDelete(String bucketName) {
        deleteObjects(bucketName);
    }

    @Override
    public void onAddNewObject(ObjectInfoDto objectInfoDto) {
        createNewVersionObject(objectInfoDto);
    }

    @Override
    public void onObjectRename(ObjectInfo oldInfo, String newName) {
        // TODO:
    }

    private void validateObjectInfoDto(ObjectInfoDto objectInfo) {
        Validate.notNull(objectInfo, "ObjectInfo cannot be null");
        Validate.notEmpty(objectInfo.objectName(), "objectName cannot be null");
        Validate.notEmpty(objectInfo.bucketName(), "bucketName cannot be null");
        Validate.notEmpty(objectInfo.fileId(), "fileId cannot be null");
    }

    private void validateObjectInfo(ObjectInfo objectInfo) {
        Validate.notNull(objectInfo, "ObjectInfo cannot be null");
        Validate.notEmpty(objectInfo.objectName(), "objectName cannot be null");
        Validate.notEmpty(objectInfo.bucketName(), "bucketName cannot be null");
    }
}
