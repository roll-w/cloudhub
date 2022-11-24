package org.huel.cloudhub.file.server.service.server;

import io.grpc.stub.StreamObserver;
import org.huel.cloudhub.file.fs.LocalFileServer;
import org.huel.cloudhub.file.fs.ServerFile;
import org.huel.cloudhub.file.fs.container.ContainerProperties;
import org.huel.cloudhub.server.ServerHostInfo;
import org.huel.cloudhub.server.ServerInfoSerializeHelper;
import org.huel.cloudhub.server.ServerStatusMonitor;
import org.huel.cloudhub.server.rpc.server.*;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author RollW
 */
@Service
public class FileServerStatusService extends ServerStatusServiceGrpc.ServerStatusServiceImplBase {
    private final ServerStatusMonitor serverStatusMonitor;

    public FileServerStatusService(ContainerProperties containerProperties,
                                   LocalFileServer localFileServer) {
        ServerFile file = localFileServer.getServerFileProvider()
                .openFile(containerProperties.getFilePath());
        this.serverStatusMonitor = new ServerStatusMonitor(file.getPath());
        this.serverStatusMonitor.setLimit(100);
        this.serverStatusMonitor.setRecordFrequency(1000);
        this.serverStatusMonitor.startMonitor();
    }

    @Override
    public void requestServerStatus(ServerStatusRequest request, StreamObserver<ServerStatusResponse> responseObserver) {
        ServerHostInfo info = serverStatusMonitor.getLatest();
        responseObserver.onNext(ServerStatusResponse.newBuilder()
                .setStatus(ServerInfoSerializeHelper.serializeFrom(info))
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public void requestServerRecordStatuses(ServerStatusRecordRequest request,
                                            StreamObserver<ServerStatusRecordResponse> responseObserver) {
        int count = request.getRecordNum();
        List<SerializedServerStatus> serverStatuses = serverStatusMonitor
                .getRecent(count)
                .stream()
                .map(ServerInfoSerializeHelper::serializeFrom)
                .toList();
        responseObserver.onNext(ServerStatusRecordResponse.newBuilder()
                .addAllStatues(serverStatuses)
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public void requestServerNetworkRecord(ServerStatusRecordRequest request, StreamObserver<ServerNetworkRecordResponse> responseObserver) {
        int count = request.getRecordNum();
        List<SerializedNetworkUsageInfo> netInfos = serverStatusMonitor
                .getRecent(count)
                .stream()
                .map(ServerInfoSerializeHelper::serializeFrom)
                .map(SerializedServerStatus::getNetInfo)
                .toList();
        responseObserver.onNext(ServerNetworkRecordResponse.newBuilder()
                .addAllNets(netInfos)
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public void requestServerDiskRecord(ServerStatusRecordRequest request, StreamObserver<ServerDiskRecordResponse> responseObserver) {
        int count = request.getRecordNum();
        List<SerializedDiskUsageInfo> diskInfos = serverStatusMonitor
                .getRecent(count)
                .stream()
                .map(ServerInfoSerializeHelper::serializeFrom)
                .map(SerializedServerStatus::getDiskInfo)
                .toList();
        responseObserver.onNext(ServerDiskRecordResponse.newBuilder()
                .addAllDisks(diskInfos)
                .build());
        responseObserver.onCompleted();
    }
}
