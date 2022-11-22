package org.huel.cloudhub.meta.server.service.server;

import io.grpc.stub.StreamObserver;
import org.huel.cloudhub.client.rpc.server.FileServerStatusRequest;
import org.huel.cloudhub.client.rpc.server.FileServerStatusResponse;
import org.huel.cloudhub.client.rpc.server.FileServerStatusServiceGrpc;
import org.huel.cloudhub.meta.server.service.file.RequestServer;
import org.huel.cloudhub.meta.server.service.node.HeartbeatService;
import org.huel.cloudhub.meta.server.service.node.ServerChecker;
import org.huel.cloudhub.server.rpc.server.SerializedFileServer;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author RollW
 */
@Service
public class NodeServerStatusService extends FileServerStatusServiceGrpc.FileServerStatusServiceImplBase {
    private final ServerChecker serverChecker;

    public NodeServerStatusService(HeartbeatService heartbeatService) {
        this.serverChecker = heartbeatService.getServerChecker();
    }

    @Override
    public void requestServers(FileServerStatusRequest request, StreamObserver<FileServerStatusResponse> responseObserver)  {
        List<SerializedFileServer> activeServers =
                RequestServer.toSerialized(serverChecker.getActiveServers());
        List<SerializedFileServer> deadServers =
                RequestServer.toSerialized(serverChecker.getDeadServers());

        FileServerStatusResponse response = FileServerStatusResponse.newBuilder()
                .addAllActiveServers(activeServers)
                .addAllDeadServers(deadServers)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
