package org.huel.cloudhub.client.disk.domain.storage.dto;

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
}
