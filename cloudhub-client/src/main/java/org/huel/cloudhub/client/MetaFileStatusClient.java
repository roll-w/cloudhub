package org.huel.cloudhub.client;

import org.huel.cloudhub.client.rpc.file.FileStatusRequest;
import org.huel.cloudhub.client.rpc.file.FileStatusResponse;
import org.huel.cloudhub.client.rpc.file.FileStatusServiceGrpc;

/**
 * @author RollW
 */
public class MetaFileStatusClient {
    private final FileStatusServiceGrpc.FileStatusServiceBlockingStub stub;

    public MetaFileStatusClient(MetaServerConnection metaServerConnection) {
        this.stub = FileStatusServiceGrpc.newBlockingStub(
                metaServerConnection.getManagedChannel());
    }

    public FileStatusResponse getFileStatus(String fileId) {
        return stub.checkFileStatus(FileStatusRequest.newBuilder()
                .setFileId(fileId)
                .build());
    }
}
