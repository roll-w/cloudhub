package org.huel.cloudhub.client.disk.domain.versioned.repository;

import org.huel.cloudhub.client.disk.database.DiskDatabase;
import org.huel.cloudhub.client.disk.database.dao.VersionedFileStorageDao;
import org.huel.cloudhub.client.disk.database.repository.BaseRepository;
import org.huel.cloudhub.client.disk.domain.versioned.VersionedFileStorage;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author RollW
 */
@Repository
public class VersionedFileRepository extends BaseRepository<VersionedFileStorage> {
    private final VersionedFileStorageDao versionedFileStorageDao;

    public VersionedFileRepository(DiskDatabase database) {
        super(database.getVersionedFileStorageDao());
        versionedFileStorageDao = database.getVersionedFileStorageDao();
    }

    public VersionedFileStorage getLatestFileVersion(long storageId) {
        return versionedFileStorageDao.getLatestFileVersion(storageId);
    }

    public List<VersionedFileStorage> getFileVersionsIncludeDelete(long storageId) {
        return versionedFileStorageDao.getFileVersionsIncludeDelete(storageId);
    }

    public List<VersionedFileStorage> getFileVersions(long storageId) {
        return versionedFileStorageDao.getFileVersions(storageId);
    }

    public VersionedFileStorage getFileVersion(long storageId, long version) {
        return versionedFileStorageDao.getFileVersion(storageId, version);
    }

    public VersionedFileStorage getById(long id) {
        return versionedFileStorageDao.getById(id);
    }
}
