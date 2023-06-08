package org.huel.cloudhub.client.disk.domain.userstorage;

import java.io.InputStream;

/**
 * @author RollW
 */
public record FileStreamInfo(
        InputStream inputStream,
        String mimeType,
        long length,
        FileType fileType
) {
}
