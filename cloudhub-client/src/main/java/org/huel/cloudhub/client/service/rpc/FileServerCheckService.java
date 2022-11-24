package org.huel.cloudhub.client.service.rpc;

import io.grpc.ManagedChannel;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.huel.cloudhub.client.data.dto.fs.ConnectedServers;
import org.huel.cloudhub.client.data.dto.fs.ContainerStatus;
import org.huel.cloudhub.client.data.dto.fs.ServerInfo;
import org.huel.cloudhub.client.rpc.server.FileServerStatusRequest;
import org.huel.cloudhub.client.rpc.server.FileServerStatusResponse;
import org.huel.cloudhub.client.rpc.server.FileServerStatusServiceGrpc;
import org.huel.cloudhub.file.rpc.container.ContainerStatusRequest;
import org.huel.cloudhub.file.rpc.container.ContainerStatusResponse;
import org.huel.cloudhub.file.rpc.container.ContainerStatusServiceGrpc;
import org.huel.cloudhub.rpc.GrpcServiceStubPool;
import org.huel.cloudhub.server.rpc.server.SerializedFileServer;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author RollW
 */
@Service
public class FileServerCheckService {
    private final FileServerStatusServiceGrpc.FileServerStatusServiceBlockingStub stub;
    private volatile FileServerStatusResponse lastResp;
    private final FileServerChannelPool fileServerChannelPool;
    private final GrpcServiceStubPool<ContainerStatusServiceGrpc.ContainerStatusServiceBlockingStub>
            containerStatusStubPool;

    public FileServerCheckService(ManagedChannel managedChannel,
                                  FileServerChannelPool fileServerChannelPool) {
        this.stub = FileServerStatusServiceGrpc.newBlockingStub(managedChannel);
        this.fileServerChannelPool = fileServerChannelPool;
        this.containerStatusStubPool = new GrpcServiceStubPool<>();
    }

    public ConnectedServers getConnectedServers() {
        lastResp = stub.requestServers(
                FileServerStatusRequest.newBuilder().build());
        List<ServerInfo> actives = deserialize(lastResp.getActiveServersList());
        List<ServerInfo> deads = deserialize(lastResp.getDeadServersList());
        return new ConnectedServers(actives, deads);
    }

    @Nullable
    public List<ContainerStatus> getContainerStatus(String id) {
        if (id == null) {
            return null;
        }
        getConnectedServers();
        List<ContainerStatus> res = trySendContainerRequest(lastResp.getActiveServersList(), id);
        if (res != null) {
            return res;
        }
        return trySendContainerRequest(lastResp.getDeadServersList(), id);
    }

    private List<ContainerStatus> trySendContainerRequest(List<SerializedFileServer> servers, String id) {
        for (SerializedFileServer server : servers) {
            if (!server.getId().equals(id)) {
                continue;
            }
            ContainerStatusServiceGrpc.ContainerStatusServiceBlockingStub
                    stub = requireContainerStatusStub(server);
            ContainerStatusResponse response =
                    stub.getContainerInfo(ContainerStatusRequest.newBuilder()
                    .build());
            return response.getInfosList().stream()
                    .map(ContainerStatus::deserialize).toList();
        }
        return null;
    }

    private ContainerStatusServiceGrpc.ContainerStatusServiceBlockingStub requireContainerStatusStub(SerializedFileServer server) {
        ContainerStatusServiceGrpc.ContainerStatusServiceBlockingStub stub =
                containerStatusStubPool.getStub(server.getId());
        if (stub != null) {
            return stub;
        }
        ManagedChannel channel = fileServerChannelPool.getChannel(server);
        stub = ContainerStatusServiceGrpc.newBlockingStub(channel);
        containerStatusStubPool.registerStub(server.getId(), stub);
        return stub;
    }

    private static List<ServerInfo> deserialize(List<SerializedFileServer> servers) {
        return servers.stream()
                .map(serializedFileServer -> new ServerInfo(
                        serializedFileServer.getHost(),
                        serializedFileServer.getId()))
                .toList();
    }

    public List<SerializedFileServer> getAllServers() {
        if (lastResp == null) {
            getConnectedServers();
        }

        List<SerializedFileServer> servers =
                new ArrayList<>(lastResp.getActiveServersList());
        servers.addAll(lastResp.getDeadServersList());
        return servers;
    }
}
