package org.huel.cloudhub.client.service.rpc;

import io.grpc.ManagedChannel;
import org.huel.cloudhub.rpc.GrpcServiceStubPool;
import org.huel.cloudhub.server.DiskUsageInfo;
import org.huel.cloudhub.server.NetworkUsageInfo;
import org.huel.cloudhub.server.ServerHostInfo;
import org.huel.cloudhub.server.ServerInfoSerializeHelper;
import org.huel.cloudhub.server.rpc.server.*;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author RollW
 */
@Service
public class ServerInfoCheckService {
    private static final int RECORDS = 50;

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

    public List<NetworkUsageInfo> getMetaServerNetRecords() {
        ServerNetworkRecordResponse response = stub.requestServerNetworkRecord(
                ServerStatusRecordRequest.newBuilder()
                        .setRecordNum(RECORDS)
                        .build()
        );
        return response.getNetsList()
                .stream()
                .map(ServerInfoSerializeHelper::deserializeFrom)
                .toList();
    }

    public List<DiskUsageInfo> getMetaServerDiskRecords() {
        ServerDiskRecordResponse response = stub.requestServerDiskRecord(
                ServerStatusRecordRequest.newBuilder()
                        .setRecordNum(RECORDS)
                        .build()
        );
        return response.getDisksList()
                .stream()
                .map(ServerInfoSerializeHelper::deserializeFrom)
                .toList();
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

    public List<DiskUsageInfo> getFileServerDiskRecords(String serverId) {
        SerializedFileServer server = getServer(serverId);
        if (server == null) {
            return null;
        }
        ServerStatusServiceGrpc.ServerStatusServiceBlockingStub stub =
                requireContainerStatusStub(server);
        if (stub == null) {
            return null;
        }
        ServerDiskRecordResponse response = stub.requestServerDiskRecord(
                ServerStatusRecordRequest.newBuilder()
                        .setRecordNum(RECORDS)
                        .build()
        );
        return response.getDisksList()
                .stream()
                .map(ServerInfoSerializeHelper::deserializeFrom)
                .toList();
    }

    public List<NetworkUsageInfo> getFileNetRecords(String serverId) {
        SerializedFileServer server = getServer(serverId);
        if (server == null) {
            return null;
        }
        ServerStatusServiceGrpc.ServerStatusServiceBlockingStub stub =
                requireContainerStatusStub(server);
        if (stub == null) {
            return null;
        }
        ServerNetworkRecordResponse response = stub.requestServerNetworkRecord(
                ServerStatusRecordRequest.newBuilder()
                        .setRecordNum(RECORDS)
                        .build()
        );
        return response.getNetsList()
                .stream()
                .map(ServerInfoSerializeHelper::deserializeFrom)
                .toList();
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
