package org.huel.cloudhub.objectstorage.data.database.repository;

import org.huel.cloudhub.objectstorage.data.database.CloudhubDatabase;
import org.huel.cloudhub.objectstorage.data.database.dao.ObjectMetadataDao;
import org.huel.cloudhub.objectstorage.data.entity.object.ObjectMetadata;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author RollW
 */
@Repository
public class ObjectMetadataRepository {
    private final ObjectMetadataDao dao;

    public ObjectMetadataRepository(CloudhubDatabase database) {
        this.dao = database.getObjectMetadataDao();
    }

    public void insert(ObjectMetadata... metadata) {
        dao.insert(metadata);
    }

    public void insert(List<ObjectMetadata> metadata) {
        dao.insert(metadata);
    }

    public void update(ObjectMetadata... metadata) {
        dao.update(metadata);
    }

    public void update(List<ObjectMetadata> metadata) {
        dao.update(metadata);
    }

    public void delete(ObjectMetadata... metadata) {
        dao.delete(metadata);
    }

    public void delete(List<ObjectMetadata> metadata) {
        dao.delete(metadata);
    }

    public void deleteByObjectName(String bucketName, String objectName) {
        dao.deleteByObjectName(bucketName, objectName);
    }

    public void deleteByBucketName(String bucketName) {
        dao.deleteByBucketName(bucketName);
    }

    public ObjectMetadata getByObjectName(String bucketName, String objectName) {
        return dao.getByObjectName(bucketName, objectName);
    }

}
