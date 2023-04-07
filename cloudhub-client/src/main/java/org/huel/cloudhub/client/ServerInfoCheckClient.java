package org.huel.cloudhub.client;

import io.grpc.ManagedChannel;
import org.huel.cloudhub.rpc.GrpcServiceStubPool;
import org.huel.cloudhub.server.DiskUsageInfo;
import org.huel.cloudhub.server.NetworkUsageInfo;
import org.huel.cloudhub.server.ServerHostInfo;
import org.huel.cloudhub.server.ServerInfoSerializeHelper;
import org.huel.cloudhub.server.rpc.server.SerializedFileServer;
import org.huel.cloudhub.server.rpc.server.ServerDiskRecordResponse;
import org.huel.cloudhub.server.rpc.server.ServerNetworkRecordResponse;
import org.huel.cloudhub.server.rpc.server.ServerStatusRecordRequest;
import org.huel.cloudhub.server.rpc.server.ServerStatusRequest;
import org.huel.cloudhub.server.rpc.server.ServerStatusResponse;
import org.huel.cloudhub.server.rpc.server.ServerStatusServiceGrpc;

import java.util.List;

/**
 * @author RollW
 */
public class ServerInfoCheckClient {
    private static final int RECORDS = 50;

    private final ServerStatusServiceGrpc.ServerStatusServiceBlockingStub stub;
    private final FileServerChannelPool fileServerChannelPool;
    private final FileServerCheckClient fileServerCheckClient;
    private final GrpcServiceStubPool<ServerStatusServiceGrpc.ServerStatusServiceBlockingStub>
            serverStatusStubPool;

    public ServerInfoCheckClient(MetaServerConnection metaServerConnection,
                                 FileServerChannelPool fileServerChannelPool,
                                 FileServerCheckClient fileServerCheckClient) {
        this.stub = ServerStatusServiceGrpc.newBlockingStub(metaServerConnection.getManagedChannel());
        this.fileServerChannelPool = fileServerChannelPool;
        this.fileServerCheckClient = fileServerCheckClient;
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
        for (SerializedFileServer server : fileServerCheckClient.getAllServers()) {
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
