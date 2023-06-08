package org.huel.cloudhub.client.disk.domain.userstorage.dto;

import org.huel.cloudhub.client.disk.domain.userstorage.FileType;

/**
 * @author RollW
 */
public record FileAttributesInfo(
        String fileName,
        String suffix,
        FileType parsedFileType,
        long size
) {
}
