package org.huel.cloudhub.client.service.object;

import org.huel.cloudhub.client.data.database.repository.ObjectMetadataRepository;
import org.huel.cloudhub.client.data.dto.object.ObjectInfo;
import org.huel.cloudhub.client.data.dto.object.ObjectInfoDto;
import org.huel.cloudhub.client.data.entity.object.ObjectMetadata;
import org.huel.cloudhub.common.ErrorCode;
import org.huel.cloudhub.common.MessagePackage;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author RollW
 */
@Service
public class ObjectMetadataServiceImpl implements ObjectMetadataService,
        ObjectRemoveHandler, ObjectChangeActionHandler {
    private final ObjectService objectService;
    private final ObjectMetadataRepository objectMetadataRepository;

    public ObjectMetadataServiceImpl(ObjectService objectService,
                                     ObjectMetadataRepository objectMetadataRepository) {
        this.objectService = objectService;
        this.objectMetadataRepository = objectMetadataRepository;
    }

    @Override
    public MessagePackage<Void> addObjectMetadata(String bucketName, String objectName,
                                                  Map<String, String> metadata) {
        if (!objectService.isObjectExist(new ObjectInfo(objectName, bucketName))) {
            return new MessagePackage<>(ErrorCode.ERROR_DATA_NOT_EXIST, null);
        }

        ObjectMetadata objectMetadata = objectMetadataRepository.getByObjectName(bucketName, objectName);
        if (objectMetadata == null) {
            ObjectMetadata newMetadata = new ObjectMetadata(bucketName, objectName, metadata);
            objectMetadataRepository.insert(newMetadata);
            return new MessagePackage<>(ErrorCode.SUCCESS, null);
        }
        objectMetadata.addAll(metadata);
        objectMetadataRepository.update(objectMetadata);
        return new MessagePackage<>(ErrorCode.SUCCESS, null);
    }

    @Override
    public Map<String, String> getObjectMetadata(String bucketName, String objectName) {
        ObjectMetadata objectMetadata = objectMetadataRepository
                .getByObjectName(bucketName, objectName);
        if (objectMetadata == null) {
            return null;
        }
        return objectMetadata.getMetadata();
    }

    @Override
    public MessagePackage<Void> removeObjectMetadata(String bucketName, String objectName, List<String> metadataKeys) {
        ObjectMetadata objectMetadata = objectMetadataRepository.getByObjectName(bucketName, objectName);
        if (objectMetadata == null) {
            return new MessagePackage<>(ErrorCode.SUCCESS, null);
        }
        metadataKeys.forEach(objectMetadata::removeKey);
        objectMetadataRepository.update(objectMetadata);
        return new MessagePackage<>(ErrorCode.SUCCESS, null);
    }

    @Override
    public void handleObjectRemove(ObjectInfoDto objectInfoDto) {
        objectMetadataRepository.deleteByObjectName(
                objectInfoDto.bucketName(), objectInfoDto.objectName());
    }

    @Override
    public void handleObjectRemove(List<ObjectInfoDto> objectInfoDtos) {
        // TODO:
    }

    @Override
    public void handleBucketDelete(String bucketName) {
        objectMetadataRepository.deleteByBucketName(bucketName);
    }

    @Override
    public void onAddNewObject(ObjectInfoDto objectInfoDto) {

    }

    @Override
    public void onObjectRename(ObjectInfo oldInfo, String newName) {
        // TODO: object rename
    }
}
