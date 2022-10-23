package org.huel.cloudhub.web.data.database.repository;

import org.huel.cloudhub.web.data.database.CloudhubDatabase;
import org.huel.cloudhub.web.data.database.dao.FileObjectStorageDao;
import org.huel.cloudhub.web.data.entity.FileObjectStorage;
import org.huel.cloudhub.web.data.entity.UserUploadFileStorage;
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
        dao.insert(storage);
    }

    @Async
    public void save(UserUploadFileStorage storage) {
        dao.insertUserUploadFileStorage(storage);
    }

    public FileObjectStorage getById(String id) {
        return dao.getByImageId(id);
    }

    public UserUploadFileStorage getByUserAndImage(Long userId, String imageId) {
        if (userId == null || imageId == null) {
            return null;
        }
        return dao.getUploadByUserAndFileId(userId, imageId);
    }
}
