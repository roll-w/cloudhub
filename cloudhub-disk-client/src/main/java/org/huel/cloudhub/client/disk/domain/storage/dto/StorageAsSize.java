package org.huel.cloudhub.client.disk.domain.storage.dto;

import org.huel.cloudhub.client.disk.domain.storage.DiskFileStorage;
import space.lingu.light.DataColumn;

/**
 * @author RollW
 */
public record StorageAsSize(
        @DataColumn(name = "file_id")
        String fileId,

        @DataColumn(name = "size")
        long fileSize
) {

        public static StorageAsSize from(DiskFileStorage diskFileStorage) {
                return new StorageAsSize(
                        diskFileStorage.getFileId(),
                        diskFileStorage.getFileSize()
                );
        }
}
