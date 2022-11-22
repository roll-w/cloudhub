package org.huel.cloudhub.client.service.rpc;

import io.grpc.ManagedChannel;
import org.huel.cloudhub.client.data.dto.fs.ConnectedServers;
import org.huel.cloudhub.client.data.dto.fs.ServerInfo;
import org.huel.cloudhub.client.rpc.server.FileServerStatusRequest;
import org.huel.cloudhub.client.rpc.server.FileServerStatusResponse;
import org.huel.cloudhub.client.rpc.server.FileServerStatusServiceGrpc;
import org.huel.cloudhub.server.rpc.server.SerializedFileServer;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author RollW
 */
@Service
public class FileServerCheckService {
    private final FileServerStatusServiceGrpc.FileServerStatusServiceBlockingStub stub;

    public FileServerCheckService(ManagedChannel managedChannel) {
        this.stub = FileServerStatusServiceGrpc.newBlockingStub(managedChannel);
    }

    public ConnectedServers getConnectedServers() {
       FileServerStatusResponse response = stub.requestServers(
               FileServerStatusRequest.newBuilder().build());
       List<ServerInfo> actives = deserialize(response.getActiveServersList());
       List<ServerInfo> deads = deserialize(response.getDeadServersList());

       return new ConnectedServers(actives, deads);
    }

    private static List<ServerInfo> deserialize(List<SerializedFileServer> servers) {
        return servers.stream()
                .map(serializedFileServer -> new ServerInfo(
                        serializedFileServer.getHost(),
                        serializedFileServer.getId()))
                .toList();
    }
}
