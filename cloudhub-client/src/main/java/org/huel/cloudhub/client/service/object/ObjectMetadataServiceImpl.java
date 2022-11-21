package org.huel.cloudhub.client.service.object;

import org.huel.cloudhub.client.data.database.repository.ObjectMetadataRepository;
import org.huel.cloudhub.client.data.dto.object.ObjectInfo;
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
public class ObjectMetadataServiceImpl implements ObjectMetadataService, ObjectRemoveHandler {
    private final ObjectMetadataRepository objectMetadataRepository;

    public ObjectMetadataServiceImpl(ObjectMetadataRepository objectMetadataRepository) {
        this.objectMetadataRepository = objectMetadataRepository;
    }

    @Override
    public MessagePackage<Void> setObjectMetadata(String bucketName, String objectName,
                                                  Map<String, String> metadata) {
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
    public void handle(ObjectInfo objectInfo) {
        objectMetadataRepository.deleteByObjectName(
                objectInfo.bucketName(), objectInfo.objectName());
    }

    @Override
    public void handle(List<ObjectInfo> objectInfos) {
        // TODO:
    }

    @Override
    public void handleBucketDelete(String bucketName) {

    }
}
