package org.huel.cloudhub.meta.server.data.database.repository;

import org.huel.cloudhub.meta.server.data.database.MetaDatabase;
import org.huel.cloudhub.meta.server.data.database.dao.FileStorageLocationDao;
import org.huel.cloudhub.meta.server.data.entity.FileStorageLocation;
import org.springframework.stereotype.Repository;

/**
 * @author RollW
 */
@Repository
public class FileStorageLocationRepository {
    private final FileStorageLocationDao dao;

    public FileStorageLocationRepository(MetaDatabase metaDatabase) {
        dao = metaDatabase.getFileObjectStorageLocationDao();
    }

    public void insertOrUpdate(FileStorageLocation storageLocation) {
        dao.insert(storageLocation);
    }

    public FileStorageLocation getByFileId(String fileId) {
        return dao.getByFileId(fileId);
    }
}
