package org.huel.cloudhub.client.data.database.repository;

import org.huel.cloudhub.client.data.database.CloudhubDatabase;
import org.huel.cloudhub.client.data.database.dao.FileObjectStorageDao;
import org.huel.cloudhub.client.data.entity.object.FileObjectStorage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

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
        // TODO:
        dao.insert(storage);
    }

    public FileObjectStorage getById(String bucketId, String objectName) {
        return dao.getObject(bucketId, objectName);
    }
}
