package org.huel.cloudhub.client.service.rpc;

import io.grpc.ManagedChannel;
import org.huel.cloudhub.client.rpc.file.FileStatusRequest;
import org.huel.cloudhub.client.rpc.file.FileStatusResponse;
import org.huel.cloudhub.client.rpc.file.FileStatusServiceGrpc;
import org.springframework.stereotype.Service;

/**
 * @author RollW
 */
@Service
public class ClientFileStatusService {
    private final FileStatusServiceGrpc.FileStatusServiceBlockingStub stub;

    public ClientFileStatusService(ManagedChannel managedChannel) {
        this.stub = FileStatusServiceGrpc.newBlockingStub(managedChannel);
    }

    public FileStatusResponse getFileStatus(String fileId) {
        return stub.checkFileStatus(FileStatusRequest.newBuilder()
                .setFileId(fileId)
                .build());
    }
}
