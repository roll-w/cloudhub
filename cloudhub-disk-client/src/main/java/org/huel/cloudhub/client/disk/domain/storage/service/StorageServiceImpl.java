package org.huel.cloudhub.client.disk.domain.storage.service;

import org.huel.cloudhub.client.conf.ClientConfigLoader;
import org.huel.cloudhub.client.disk.domain.storage.DiskFileStorage;
import org.huel.cloudhub.client.disk.domain.storage.StorageService;
import org.huel.cloudhub.client.disk.domain.storage.repository.DiskFileStorageRepository;
import org.huel.cloudhub.fs.CFSClient;
import org.huel.cloudhub.fs.FileValidation;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author RollW
 */
@Service
public class StorageServiceImpl implements StorageService {
    private final DiskFileStorageRepository diskFileStorageRepository;
    private final CFSClient cfsClient;
    private final ClientConfigLoader clientConfigLoader;

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
        cfsClient.downloadFile(outputStream, fileId);
    }

    @Override
    public void getFile(String fileId, OutputStream outputStream,
                        long startBytes, long endBytes) throws IOException {
        cfsClient.downloadFile(outputStream, fileId, startBytes, endBytes);
    }
}
