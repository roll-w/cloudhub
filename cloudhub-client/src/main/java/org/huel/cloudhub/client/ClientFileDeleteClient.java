package org.huel.cloudhub.client;

import org.huel.cloudhub.client.rpc.file.ClientFileDeleteRequest;
import org.huel.cloudhub.client.rpc.file.ClientFileDeleteResponse;
import org.huel.cloudhub.client.rpc.file.ClientFileDeleteServiceGrpc;

/**
 * @author RollW
 */
public class ClientFileDeleteClient {
    private final ClientFileDeleteServiceGrpc.ClientFileDeleteServiceBlockingStub stub;

    public ClientFileDeleteClient(MetaServerConnection connection) {
        this.stub = ClientFileDeleteServiceGrpc.newBlockingStub(connection.getManagedChannel());
    }

    public void permanentlyDeleteFile(String fileId) {
        ClientFileDeleteResponse response = stub.deleteFile(
                ClientFileDeleteRequest.newBuilder()
                        .setFileId(fileId)
                        .build()
        );
    }
}
