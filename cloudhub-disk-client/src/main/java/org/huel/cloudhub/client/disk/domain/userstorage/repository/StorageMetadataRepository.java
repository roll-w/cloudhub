package org.huel.cloudhub.client.disk.domain.userstorage.repository;

import org.huel.cloudhub.client.disk.database.DiskDatabase;
import org.huel.cloudhub.client.disk.database.dao.StorageMetadataDao;
import org.huel.cloudhub.client.disk.database.repository.BaseRepository;
import org.huel.cloudhub.client.disk.domain.systembased.ContextThreadAware;
import org.huel.cloudhub.client.disk.domain.systembased.paged.PageableContext;
import org.huel.cloudhub.client.disk.domain.tag.NameValue;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageMetadata;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author RollW
 */
@Repository
public class StorageMetadataRepository extends BaseRepository<StorageMetadata> {
    private final StorageMetadataDao storageMetadataDao;

    public StorageMetadataRepository(DiskDatabase database,
                                     ContextThreadAware<PageableContext> pageableContextThreadAware,
                                     CacheManager cacheManager) {
        super(database.getStorageMetadataDao(), pageableContextThreadAware, cacheManager);
        storageMetadataDao = database.getStorageMetadataDao();
    }

    public List<StorageMetadata> getByStorageId(long storageId) {
        List<StorageMetadata> storageMetadata =
                storageMetadataDao.getByStorageId(storageId);
        return cacheResult(storageMetadata);
    }

    public StorageMetadata getByStorageIdAndTagGroupId(long storageId, long tagGroupId) {
        StorageMetadata storageMetadata =
                storageMetadataDao.getByStorageIdAndTagGroupId(storageId, tagGroupId);
        return cacheResult(storageMetadata);
    }

    public List<StorageMetadata> getByTagId(long tagId) {
        List<StorageMetadata> storageMetadata =
                storageMetadataDao.getByTagId(tagId);
        return cacheResult(storageMetadata);
    }

    public List<StorageMetadata> getByTagValues(List<NameValue> nameValues) {
        List<StorageMetadata> storageMetadata =
                storageMetadataDao.getByTagValues(nameValues);
        return cacheResult(storageMetadata);
    }

    public StorageMetadata getByStorageIdAndName(long storageId, String name) {
        StorageMetadata storageMetadata =
                storageMetadataDao.getByStorageIdAndName(storageId, name);
        return cacheResult(storageMetadata);
    }

    @Override
    protected Class<StorageMetadata> getEntityClass() {
        return StorageMetadata.class;
    }
}
