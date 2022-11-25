package org.huel.cloudhub.client.service.rpc;

import io.grpc.ManagedChannel;
import org.huel.cloudhub.client.rpc.file.ClientFileDeleteRequest;
import org.huel.cloudhub.client.rpc.file.ClientFileDeleteResponse;
import org.huel.cloudhub.client.rpc.file.ClientFileDeleteServiceGrpc;
import org.springframework.stereotype.Service;

/**
 * @author RollW
 */
@Service
public class ClientFileDeleteService {
    private final ClientFileDeleteServiceGrpc.ClientFileDeleteServiceBlockingStub stub;

    public ClientFileDeleteService(ManagedChannel channel) {
        this.stub = ClientFileDeleteServiceGrpc.newBlockingStub(channel);
    }

    public void permanentlyDeleteFile(String fileId) {
        ClientFileDeleteResponse response = stub.deleteFile(
                ClientFileDeleteRequest.newBuilder()
                        .setFileId(fileId)
                        .build()
        );
    }
}
