package org.huel.cloudhub.client.data.database.repository;

import org.huel.cloudhub.client.data.database.CloudhubDatabase;
import org.huel.cloudhub.client.data.database.dao.VersionedObjectDao;
import org.springframework.stereotype.Repository;

/**
 * @author RollW
 */
@Repository
public class VersionedObjectRepository {
    private final VersionedObjectDao versionedObjectDao;

    public VersionedObjectRepository(CloudhubDatabase database) {
        this.versionedObjectDao = database.getVersionedObjectDao();
    }
}
