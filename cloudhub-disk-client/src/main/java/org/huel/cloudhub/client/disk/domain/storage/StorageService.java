package org.huel.cloudhub.client.disk.domain.storage;

import org.huel.cloudhub.client.disk.domain.storage.dto.StorageAsSize;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * @author RollW
 */
public interface StorageService {
    String saveFile(InputStream inputStream) throws IOException;

    void getFile(String fileId, OutputStream outputStream) throws IOException;

    void getFile(String fileId, OutputStream outputStream, long startBytes, long endBytes) throws IOException;

    List<StorageAsSize> getFileSizes(List<String> fileIds);

    long getFileSize(String fileId);
}
