package org.huel.cloudhub.client.disk.domain.versioned;

import java.util.List;

/**
 * @author RollW
 */
public interface VersionedFileService {
    VersionedFileStorage getVersionedFileStorage(long versionedFileStorageId);

    List<VersionedFileStorage> getVersionedFileStorages(long fileStorageId);

    void deleteVersionedFileStorage(long versionedFileStorageId);

    void deleteVersionedFileStorage(long storageId, long version);
}
