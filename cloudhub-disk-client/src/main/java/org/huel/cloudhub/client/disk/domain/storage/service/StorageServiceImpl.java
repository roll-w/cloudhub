package org.huel.cloudhub.client.disk.domain.storage.service;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.huel.cloudhub.client.CFSStatus;
import org.huel.cloudhub.client.conf.ClientConfigLoader;
import org.huel.cloudhub.client.disk.domain.storage.DiskFileStorage;
import org.huel.cloudhub.client.disk.domain.storage.StorageService;
import org.huel.cloudhub.client.disk.domain.storage.dto.StorageAsSize;
import org.huel.cloudhub.client.disk.domain.storage.repository.DiskFileStorageRepository;
import org.huel.cloudhub.client.CFSClient;
import org.huel.cloudhub.client.FileValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * @author RollW
 */
@Service
public class StorageServiceImpl implements StorageService {
    private final DiskFileStorageRepository diskFileStorageRepository;
    private final CFSClient cfsClient;
    private final ClientConfigLoader clientConfigLoader;

    private static final Logger logger = LoggerFactory.getLogger(StorageServiceImpl.class);

    public StorageServiceImpl(DiskFileStorageRepository diskFileStorageRepository,
                              CFSClient cfsClient,
                              ClientConfigLoader clientConfigLoader) {
        this.diskFileStorageRepository = diskFileStorageRepository;
        this.cfsClient = cfsClient;
        this.clientConfigLoader = clientConfigLoader;
    }

    @Override
    public String saveFile(InputStream inputStream) throws IOException {
        FileValidation fileValidation =
                cfsClient.uploadFile(inputStream, clientConfigLoader.getTempFilePath());
        IOUtils.closeQuietly(inputStream);
        long time = System.currentTimeMillis();
        DiskFileStorage exist = diskFileStorageRepository.getById(fileValidation.id());
        if (exist != null) {
            return exist.getFileId();
        }
        DiskFileStorage diskFileStorage = new DiskFileStorage(
                fileValidation.id(),
                fileValidation.size(),
                time,
                time
        );
        diskFileStorageRepository.insert(diskFileStorage);

        return diskFileStorage.getFileId();
    }

    @Override
    public void getFile(String fileId, OutputStream outputStream) throws IOException {
        CFSStatus status = cfsClient.downloadFile(outputStream, fileId);
        if (!status.success()) {
            logger.debug("Download file error, fileId: {}, status: {}", fileId, status);
        }
    }

    @Override
    public void getFile(String fileId, OutputStream outputStream,
                        long startBytes, long endBytes) throws IOException {
        CFSStatus status =
                cfsClient.downloadFile(outputStream, fileId, startBytes, endBytes);
        if (!status.success()) {
            logger.debug("Download file error, fileId: {}, startBytes: {}, endBytes: {}, status: {}",
                    fileId, startBytes, endBytes, status);
        }
    }

    @Override
    public List<StorageAsSize> getFileSizes(List<String> fileIds) {
        return diskFileStorageRepository.getSizesByIds(fileIds);
    }

    @Override
    public long getFileSize(String fileId) {
        return diskFileStorageRepository.getSizeById(fileId);
    }

}
