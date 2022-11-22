package org.huel.cloudhub.file.server.service.server;

import io.grpc.stub.StreamObserver;
import org.huel.cloudhub.file.fs.container.ContainerProperties;
import org.huel.cloudhub.server.ServerHostInfo;
import org.huel.cloudhub.server.ServerInfoSerializeHelper;
import org.huel.cloudhub.server.rpc.server.ServerStatusRequest;
import org.huel.cloudhub.server.rpc.server.ServerStatusResponse;
import org.huel.cloudhub.server.rpc.server.ServerStatusServiceGrpc;
import org.springframework.stereotype.Service;

/**
 * @author RollW
 */
@Service
public class FileServerStatusService extends ServerStatusServiceGrpc.ServerStatusServiceImplBase {
    private final ServerHostInfo serverHostInfo;

    public FileServerStatusService(ContainerProperties containerProperties) {
        this.serverHostInfo = ServerHostInfo.load(containerProperties.getFilePath());
    }

    @Override
    public void requestServerStatus(ServerStatusRequest request, StreamObserver<ServerStatusResponse> responseObserver) {
        ServerHostInfo info = serverHostInfo.reload();
        responseObserver.onNext(ServerStatusResponse.newBuilder()
                .setStatus(ServerInfoSerializeHelper.serializeFrom(info))
                .build());
        responseObserver.onCompleted();
    }
}
