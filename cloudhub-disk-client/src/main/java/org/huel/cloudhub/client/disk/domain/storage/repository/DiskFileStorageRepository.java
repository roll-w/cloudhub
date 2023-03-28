package org.huel.cloudhub.client.disk.domain.storage.repository;

import org.huel.cloudhub.client.disk.database.DiskDatabase;
import org.huel.cloudhub.client.disk.database.dao.DiskFileStorageDao;
import org.huel.cloudhub.client.disk.domain.storage.DiskFileStorage;
import org.huel.cloudhub.web.data.page.Offset;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author RollW
 */
@Repository
public class DiskFileStorageRepository {
    private final DiskFileStorageDao diskFileStorageDao;

    public DiskFileStorageRepository(DiskDatabase diskDatabase) {
        diskFileStorageDao = diskDatabase.getDiskFileStorageDao();
    }

    public void insert(DiskFileStorage diskFileStorages) {
        diskFileStorageDao.insert(diskFileStorages);
    }

    public void update(DiskFileStorage diskFileStorages) {
        diskFileStorageDao.update(diskFileStorages);
    }

    public void delete(DiskFileStorage DiskFileStorage) {
        diskFileStorageDao.delete(DiskFileStorage);
    }

    public List<DiskFileStorage> get() {
        return diskFileStorageDao.get();
    }

    public List<DiskFileStorage> get(Offset offset) {
        return diskFileStorageDao.get(offset);
    }

    public DiskFileStorage getById(String fileId) {
        return diskFileStorageDao.getById(fileId);
    }
}
