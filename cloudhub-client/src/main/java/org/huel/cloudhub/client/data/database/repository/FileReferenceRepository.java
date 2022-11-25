package org.huel.cloudhub.client.data.database.repository;

import org.huel.cloudhub.client.data.database.CloudhubDatabase;
import org.huel.cloudhub.client.data.database.dao.FileReferenceDao;
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
}
