package org.huel.cloudhub.client.service.rpc;

import io.grpc.ManagedChannel;
import org.huel.cloudhub.rpc.GrpcServiceStubPool;
import org.huel.cloudhub.server.ServerHostInfo;
import org.huel.cloudhub.server.ServerInfoSerializeHelper;
import org.huel.cloudhub.server.rpc.server.SerializedFileServer;
import org.huel.cloudhub.server.rpc.server.ServerStatusRequest;
import org.huel.cloudhub.server.rpc.server.ServerStatusResponse;
import org.huel.cloudhub.server.rpc.server.ServerStatusServiceGrpc;
import org.springframework.stereotype.Service;

/**
 * @author RollW
 */
@Service
public class ServerInfoCheckService {
    private final ServerStatusServiceGrpc.ServerStatusServiceBlockingStub stub;
    private final FileServerChannelPool fileServerChannelPool;
    private final FileServerCheckService fileServerCheckService;
    private final GrpcServiceStubPool<ServerStatusServiceGrpc.ServerStatusServiceBlockingStub>
            serverStatusStubPool;

    public ServerInfoCheckService(ManagedChannel channel,
                                  FileServerChannelPool fileServerChannelPool,
                                  FileServerCheckService fileServerCheckService) {
        this.stub = ServerStatusServiceGrpc.newBlockingStub(channel);
        this.fileServerChannelPool = fileServerChannelPool;
        this.fileServerCheckService = fileServerCheckService;
        this.serverStatusStubPool = new GrpcServiceStubPool<>();
    }

    public ServerHostInfo getMetaServerInfo() {
        ServerStatusResponse response = stub.requestServerStatus(
                ServerStatusRequest.newBuilder().build()
        );
        return ServerInfoSerializeHelper.deserializeFrom(response.getStatus());
    }

    public ServerHostInfo getFileServerInfo(String serverId) {
        SerializedFileServer server = getServer(serverId);
        if (server == null) {
            return null;
        }
        ServerStatusServiceGrpc.ServerStatusServiceBlockingStub stub =
                requireContainerStatusStub(server);
        if (stub == null) {
            return null;
        }
        ServerStatusResponse response = stub.requestServerStatus(
                ServerStatusRequest.newBuilder().build()
        );
        return ServerInfoSerializeHelper.deserializeFrom(response.getStatus());
    }

    private SerializedFileServer getServer(String serverId) {
        for (SerializedFileServer server : fileServerCheckService.getAllServers()) {
            if (!server.getId().equals(serverId)) {
                continue;
            }
            return server;
        }
        return null;
    }

    private ServerStatusServiceGrpc.ServerStatusServiceBlockingStub requireContainerStatusStub(SerializedFileServer server) {
        ServerStatusServiceGrpc.ServerStatusServiceBlockingStub stub =
                serverStatusStubPool.getStub(server.getId());
        if (stub != null) {
            return stub;
        }
        ManagedChannel channel = fileServerChannelPool.getChannel(server);
        if (channel == null) {
            return null;
        }
        stub = ServerStatusServiceGrpc.newBlockingStub(channel);
        serverStatusStubPool.registerStub(server.getId(), stub);
        return stub;
    }

}
