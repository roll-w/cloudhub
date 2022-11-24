package org.huel.cloudhub.client.data.database.repository;

import org.huel.cloudhub.client.data.database.CloudhubDatabase;
import org.huel.cloudhub.client.data.database.dao.FileObjectStorageDao;
import org.huel.cloudhub.client.data.dto.object.ObjectInfoDto;
import org.huel.cloudhub.client.data.entity.object.FileObjectStorage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author RollW
 */
@Repository
public class FileObjectStorageRepository {
    private final FileObjectStorageDao dao;

    public FileObjectStorageRepository(CloudhubDatabase database) {
        dao = database.getFileObjectStorageDao();
    }

    @Async
    public void save(FileObjectStorage storage) {
        dao.insert(storage);
    }

    @Async
    public void update(FileObjectStorage storage) {
        dao.update(storage);
    }

    @Async
    public void delete(FileObjectStorage... storages) {
        dao.delete(storages);
    }

    @Async
    public void deleteByBucketWithObjects(String bucketName, Collection<String> objectNames) {
        dao.deleteByBucketNameWith(bucketName, new ArrayList<>(objectNames));
    }

    @Async
    public void deleteByBucketName(String bucketName) {
        dao.deleteByBucketName(bucketName);
    }

    @Async
    public void deleteByBucketNameAnd(String bucketName, String objectName) {
        dao.deleteByObjectName(bucketName, objectName);
    }

    public FileObjectStorage getById(String bucketId, String objectName) {
        return dao.getObject(bucketId, objectName);
    }

    public List<FileObjectStorage> getByBucketName(String bucketId) {
        return dao.getObjectsByBucketName(bucketId);
    }

    public List<ObjectInfoDto> getObjectInfoDtosByBucketName(String bucketId) {
        return dao.getObjectInfoDtosByBucketName(bucketId);
    }

    public boolean isObjectExist(String bucketName, String objectName) {
        return dao.getBucketHasObject(bucketName, objectName) != null;
    }

    public int getAllObjectsCount() {
        return dao.getObjectCount();
    }
}
