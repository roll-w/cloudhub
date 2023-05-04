package org.huel.cloudhub.client.disk.domain.userstorage.repository;

import org.huel.cloudhub.client.disk.database.DiskDatabase;
import org.huel.cloudhub.client.disk.database.dao.StorageMetadataDao;
import org.huel.cloudhub.client.disk.database.repository.BaseRepository;
import org.huel.cloudhub.client.disk.domain.tag.dto.TagValue;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageMetadata;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author RollW
 */
@Repository
public class StorageMetadataRepository extends BaseRepository<StorageMetadata> {
    private final StorageMetadataDao storageMetadataDao;

    public StorageMetadataRepository(DiskDatabase database) {
        super(database.getStorageMetadataDao());
        storageMetadataDao = database.getStorageMetadataDao();
    }

    public List<StorageMetadata> getByStorageId(long storageId) {
        return storageMetadataDao.getByStorageId(storageId);
    }

    public StorageMetadata getByStorageIdAndTagGroupId(long storageId, long tagGroupId) {
        return storageMetadataDao.getByStorageIdAndTagGroupId(storageId, tagGroupId);
    }

    public List<StorageMetadata> getByTagId(long tagId) {
        return storageMetadataDao.getByTagId(tagId);
    }

    public List<StorageMetadata> getByTagValues(List<TagValue> tagValues) {
        return storageMetadataDao.getByTagValues(tagValues);
    }
}
