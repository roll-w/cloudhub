package org.huel.cloudhub.meta.server.data.database.repository;

import org.huel.cloudhub.meta.server.data.database.MetaDatabase;
import org.huel.cloudhub.meta.server.data.database.dao.FileStorageLocationDao;
import org.huel.cloudhub.meta.server.data.entity.FileStorageLocation;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import java.util.List;

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

    @Async
    public void delete(String fileId) {
        dao.deleteById(fileId);
    }

    @Async
    public void delete(FileStorageLocation... location) {
        dao.delete(location);
    }

    public List<FileStorageLocation> getLocationsByFileIdDesc(String fileId) {
        return dao.getLocationsByFileIdDesc(fileId);
    }

    public List<FileStorageLocation> getLocationsByFileId(String fileId) {
        return dao.getLocationsByFileId(fileId);
    }

    public List<FileStorageLocation> getLocationsByServerId(String serverId) {
        return dao.getLocationsByServerId(serverId);
    }
}
