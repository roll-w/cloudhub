package org.huel.cloudhub.objectstorage.service.object;

import org.huel.cloudhub.objectstorage.data.database.repository.ObjectMetadataRepository;
import org.huel.cloudhub.objectstorage.data.dto.object.ObjectInfo;
import org.huel.cloudhub.objectstorage.data.dto.object.ObjectInfoDto;
import org.huel.cloudhub.objectstorage.data.entity.object.ObjectMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final Logger logger = LoggerFactory.getLogger(ObjectMetadataServiceImpl.class);

    public ObjectMetadataServiceImpl(ObjectService objectService,
                                     ObjectMetadataRepository objectMetadataRepository) {
        this.objectService = objectService;
        this.objectMetadataRepository = objectMetadataRepository;
    }

    @Override
    public void addObjectMetadataWithCheck(String bucketName, String objectName, Map<String, String> metadata) {
        if (!objectService.isObjectExist(new ObjectInfo(objectName, bucketName))) {
            throw new ObjectRuntimeException(ObjectErrorCode.ERROR_OBJECT_NOT_EXIST);
        }
        tryAddObjectMetadata(bucketName, objectName, metadata, false);
    }

    @Override
    public void addObjectMetadata(String bucketName, String objectName,
                                  Map<String, String> metadata) {
        tryAddObjectMetadata(bucketName, objectName, metadata, true);
    }

    private void tryAddObjectMetadata(String bucketName, String objectName,
                                                      Map<String, String> metadata, boolean force) {
        ObjectMetadata objectMetadata = objectMetadataRepository.getByObjectName(bucketName, objectName);
        if (objectMetadata == null) {
            logger.debug("Insert with new metadata, bucket={};object={};metadata={}", bucketName, objectName, metadata);
            ObjectMetadata newMetadata = new ObjectMetadata(bucketName, objectName, metadata);
            objectMetadataRepository.insert(newMetadata);
            return;
        }
        objectMetadata.addAll(metadata, force);
        objectMetadataRepository.update(objectMetadata);
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
    public void removeObjectMetadata(String bucketName, String objectName, List<String> metadataKeys) {
        ObjectMetadata objectMetadata = objectMetadataRepository.getByObjectName(bucketName, objectName);
        if (objectMetadata == null) {
            return;
        }
        metadataKeys.forEach(objectMetadata::removeKey);
        objectMetadataRepository.update(objectMetadata);
    }

    @Override
    public void handleObjectRemove(ObjectInfoDto objectInfoDto) {
        logger.debug("Object remove, bucketName={};objectName={}",
                objectInfoDto.bucketName(), objectInfoDto.objectName());
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
