package org.huel.cloudhub.meta.server.service.file;

import io.grpc.stub.StreamObserver;
import org.huel.cloudhub.client.rpc.file.FileStatusRequest;
import org.huel.cloudhub.client.rpc.file.FileStatusResponse;
import org.huel.cloudhub.client.rpc.file.FileStatusServiceGrpc;
import org.huel.cloudhub.meta.server.data.database.repository.FileStorageLocationRepository;
import org.huel.cloudhub.meta.server.data.entity.FileStorageLocation;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author RollW
 */
@Service
public class FileStatusService extends FileStatusServiceGrpc.FileStatusServiceImplBase {
    private final FileStorageLocationRepository fileStorageLocationRepository;

    public FileStatusService(FileStorageLocationRepository fileStorageLocationRepository) {
        this.fileStorageLocationRepository = fileStorageLocationRepository;
    }

    @Override
    public void checkFileStatus(FileStatusRequest request, StreamObserver<FileStatusResponse> responseObserver) {
        String fileId = request.getFileId();
        FileStorageLocation location =
                fileStorageLocationRepository.getByFileId(fileId);
        if (location == null) {
            responseObserver.onNext(FileStatusResponse.newBuilder()
                    .setMasterId("")
                    .build());
            responseObserver.onCompleted();
            return;
        }
        List<FileStorageLocation> locations =
                fileStorageLocationRepository.getLocationsByFileId(fileId);
        // TODO: get active servers
    }


}
