package org.huel.cloudhub.meta.server.service.file;

import io.grpc.stub.StreamObserver;
import org.huel.cloudhub.client.rpc.file.ClientFileDeleteRequest;
import org.huel.cloudhub.client.rpc.file.ClientFileDeleteResponse;
import org.huel.cloudhub.client.rpc.file.ClientFileDeleteServiceGrpc;
import org.springframework.stereotype.Service;

/**
 * @author RollW
 */
@Service
public class ClientFileDeleteDispatchService extends ClientFileDeleteServiceGrpc.ClientFileDeleteServiceImplBase {
    private final FileDeleteService fileDeleteService;

    public ClientFileDeleteDispatchService(FileDeleteService fileDeleteService) {
        this.fileDeleteService = fileDeleteService;
    }

    @Override
    public void deleteFile(ClientFileDeleteRequest request,
                           StreamObserver<ClientFileDeleteResponse> responseObserver) {
        final String fileId = request.getFileId();
        if (fileId.isEmpty()) {
            responseObserver.onError(
                    new IllegalArgumentException("File id is null or empty")
            );
            return;
        }
        fileDeleteService.deleteFileCompletely(fileId);
        responseObserver.onCompleted();
    }
}
