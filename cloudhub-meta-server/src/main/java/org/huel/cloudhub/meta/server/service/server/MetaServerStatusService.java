package org.huel.cloudhub.meta.server.service.server;

import io.grpc.stub.StreamObserver;
import org.huel.cloudhub.meta.server.configuration.FileProperties;
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
public class MetaServerStatusService extends ServerStatusServiceGrpc.ServerStatusServiceImplBase {
    private final ServerHostInfo serverHostInfo;

    public MetaServerStatusService(FileProperties fileProperties) {
        this.serverHostInfo = ServerHostInfo.load(fileProperties.getDataPath());
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
