package org.huel.cloudhub.client.data.database.repository;

import org.huel.cloudhub.client.data.database.CloudhubDatabase;
import org.huel.cloudhub.client.data.database.dao.VersionedObjectDao;
import org.huel.cloudhub.client.data.entity.object.VersionedObject;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author RollW
 */
@Repository
public class VersionedObjectRepository {
    private final VersionedObjectDao versionedObjectDao;

    public VersionedObjectRepository(CloudhubDatabase database) {
        this.versionedObjectDao = database.getVersionedObjectDao();
    }

    @Async
    public void insert(VersionedObject versionedObject) {
        versionedObjectDao.insert(versionedObject);
    }

    @Async
    public void update(VersionedObject versionedObject) {
        versionedObjectDao.update(versionedObject);
    }

    @Async
    public void delete(VersionedObject versionedObject) {
        versionedObjectDao.delete(versionedObject);
    }

    @Async
    public void deleteVersion(String bucketName, String objectName, long version) {
        versionedObjectDao.deleteByVersion(bucketName, objectName, version);
    }

    public void deleteObjects(String bucketName, String objectName) {
    }

    public void deleteObjects(String bucketName) {
    }

    public VersionedObject getLatestObject(String bucketName, String objectName) {
        return versionedObjectDao.getLatestObjectVersion(bucketName, objectName);
    }

    public VersionedObject getVersionedObject(String bucketName, String objectName, long version) {
        return versionedObjectDao.getObjectVersion(bucketName, objectName, version);
    }

    public List<VersionedObject> getVersionedObjects(String bucketName, String objectName) {
        return versionedObjectDao.getObjectsVersion(bucketName, objectName);
    }

    public void updateObjects(List<VersionedObject> objects) {
        versionedObjectDao.update(objects);
    }
}
