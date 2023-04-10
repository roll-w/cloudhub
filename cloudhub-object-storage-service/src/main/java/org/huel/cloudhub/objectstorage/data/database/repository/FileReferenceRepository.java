package org.huel.cloudhub.objectstorage.data.database.repository;

import org.huel.cloudhub.objectstorage.data.database.CloudhubDatabase;
import org.huel.cloudhub.objectstorage.data.database.dao.FileReferenceDao;
import org.huel.cloudhub.objectstorage.data.entity.object.FileReference;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

/**
 * @author RollW
 */
@Repository
public class FileReferenceRepository {
    private final FileReferenceDao fileReferenceDao;

    public FileReferenceRepository(CloudhubDatabase database) {
        this.fileReferenceDao = database.getFileReferenceDao();
    }

    @Async
    public void insert(FileReference reference) {
        fileReferenceDao.insert(reference);
    }

    public void update(FileReference reference) {

    }

    public FileReference getReference(String fileId) {
        return fileReferenceDao.getReference(fileId);
    }
}
