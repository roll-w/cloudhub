package org.huel.cloudhub.meta.server.service.file;

import io.grpc.stub.StreamObserver;
import org.huel.cloudhub.client.rpc.file.FileStatusRequest;
import org.huel.cloudhub.client.rpc.file.FileStatusResponse;
import org.huel.cloudhub.client.rpc.file.FileStatusServiceGrpc;
import org.huel.cloudhub.meta.server.data.database.repository.FileStorageLocationRepository;
import org.huel.cloudhub.meta.server.data.entity.FileStorageLocation;
import org.huel.cloudhub.meta.server.service.node.HeartbeatService;
import org.huel.cloudhub.meta.server.service.node.NodeAllocator;
import org.huel.cloudhub.meta.server.service.node.NodeServer;
import org.huel.cloudhub.meta.server.service.node.ServerChecker;
import org.huel.cloudhub.server.rpc.server.SerializedFileServer;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author RollW
 */
@Service
public class FileStatusService extends FileStatusServiceGrpc.FileStatusServiceImplBase {
    private final FileStorageLocationRepository fileStorageLocationRepository;
    private final ServerChecker serverChecker;
    private final NodeAllocator nodeAllocator;

    public FileStatusService(FileStorageLocationRepository fileStorageLocationRepository,
                             HeartbeatService heartbeatService) {
        this.fileStorageLocationRepository = fileStorageLocationRepository;
        this.serverChecker = heartbeatService.getServerChecker();
        this.nodeAllocator = heartbeatService.getNodeAllocator();
    }

    @Override
    public void checkFileStatus(FileStatusRequest request, StreamObserver<FileStatusResponse> responseObserver) {
        String fileId = request.getFileId();
        FileStorageLocation location =
                fileStorageLocationRepository.getByFileId(fileId);
        if (location == null) {
            responseObserver.onNext(FileStatusResponse.newBuilder()
                    .setMasterId("")
                    .build());
            responseObserver.onCompleted();
            return;
        }
        List<FileStorageLocation> locations =
                fileStorageLocationRepository.getLocationsByFileId(fileId);
        ActiveServers activeServers = getActiveServers(locations);
        responseObserver.onNext(FileStatusResponse.newBuilder()
                .setMasterId(activeServers.masterId())
                .addAllServers(activeServers.servers())
                .build());
        responseObserver.onCompleted();
        // TODO: get active servers
    }

    private record ActiveServers(
            List<SerializedFileServer> servers,
            String masterId
    ) {
    }

    private ActiveServers getActiveServers(List<FileStorageLocation> locations) {
        for (FileStorageLocation location : locations) {
            List<SerializedFileServer> servers = checkServers(location);
            if (!servers.isEmpty()) {
                return new ActiveServers(servers, location.getMasterServerId());
            }
        }
        return new ActiveServers(List.of(), "");
    }

    private List<SerializedFileServer> checkServers(FileStorageLocation location) {
        List<SerializedFileServer> servers = new ArrayList<>();
        for (String s : location.getServerList()) {
            // select all active servers.
            if (!serverChecker.isActive(s)) {
                continue;
            }
            NodeServer server = nodeAllocator.findNodeServer(s);
            if (server == null) {
                continue;
            }
            servers.add(RequestServer.toSerialized(server));
        }
        return servers;
    }
}
