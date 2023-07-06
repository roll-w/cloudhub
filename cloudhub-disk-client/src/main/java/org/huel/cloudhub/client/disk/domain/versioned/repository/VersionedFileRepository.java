package org.huel.cloudhub.client.disk.domain.versioned.repository;

import org.huel.cloudhub.client.disk.database.DiskDatabase;
import org.huel.cloudhub.client.disk.database.dao.VersionedFileStorageDao;
import org.huel.cloudhub.client.disk.database.repository.BaseRepository;
import org.huel.cloudhub.client.disk.domain.systembased.ContextThreadAware;
import org.huel.cloudhub.client.disk.domain.systembased.paged.PageableContext;
import org.huel.cloudhub.client.disk.domain.versioned.VersionedFileStorage;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author RollW
 */
@Repository
public class VersionedFileRepository extends BaseRepository<VersionedFileStorage> {
    private final VersionedFileStorageDao versionedFileStorageDao;

    public VersionedFileRepository(DiskDatabase database,
                                   ContextThreadAware<PageableContext> pageableContextThreadAware,
                                   CacheManager cacheManager) {
        super(database.getVersionedFileStorageDao(), pageableContextThreadAware, cacheManager);
        versionedFileStorageDao = database.getVersionedFileStorageDao();
    }

    public VersionedFileStorage getLatestFileVersion(long storageId) {
        return cacheResult(
                versionedFileStorageDao.getLatestFileVersion(storageId)
        );
    }

    public List<VersionedFileStorage> getFileVersionsIncludeDelete(long storageId) {
        return cacheResult(
                versionedFileStorageDao.getFileVersionsIncludeDelete(storageId)
        );
    }

    public List<VersionedFileStorage> getFileVersions(long storageId) {
        return cacheResult(
                versionedFileStorageDao.getFileVersions(storageId)
        );
    }

    public VersionedFileStorage getFileVersion(long storageId, long version) {
        return cacheResult(
                versionedFileStorageDao.getFileVersion(storageId, version)
        );
    }

    @Override
    protected Class<VersionedFileStorage> getEntityClass() {
        return VersionedFileStorage.class;
    }
}
