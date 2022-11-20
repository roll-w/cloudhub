package org.huel.cloudhub.meta.server.service.file;

import io.grpc.ManagedChannel;
import io.grpc.stub.StreamObserver;
import org.huel.cloudhub.file.rpc.block.BlockDeleteServiceGrpc;
import org.huel.cloudhub.file.rpc.block.DeleteBlocksRequest;
import org.huel.cloudhub.file.rpc.block.DeleteBlocksResponse;
import org.huel.cloudhub.meta.server.data.database.repository.FileStorageLocationRepository;
import org.huel.cloudhub.meta.server.data.entity.FileStorageLocation;
import org.huel.cloudhub.meta.server.service.node.*;
import org.huel.cloudhub.rpc.GrpcServiceStubPool;
import org.huel.cloudhub.server.rpc.server.SerializedFileServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author RollW
 */
@Service
public class FileDeleteService {
    private final NodeAllocator nodeAllocator;
    private final ServerChecker serverChecker;
    private final FileStorageLocationRepository repository;
    private final NodeChannelPool nodeChannelPool;
    private final GrpcServiceStubPool<BlockDeleteServiceGrpc.BlockDeleteServiceStub>
            blockDeleteServiceStubPool;
    private final Logger logger = LoggerFactory.getLogger(FileDeleteService.class);

    public FileDeleteService(HeartbeatService heartbeatService,
                             FileStorageLocationRepository repository,
                             NodeChannelPool nodeChannelPool) {
        this.nodeAllocator = heartbeatService.getNodeAllocator();
        this.serverChecker = heartbeatService.getServerChecker();
        this.repository = repository;
        this.nodeChannelPool = nodeChannelPool;
        this.blockDeleteServiceStubPool = new GrpcServiceStubPool<>();
    }

    public void deleteFileCompletely(String fileId) {
        FileStorageLocation location = repository.getByFileId(fileId);
        if (location == null) {
            logger.debug("File not exist, fileId={}", fileId);
            return;
        }
        String master = location.getMasterServerId();
        String[] replicas = location.getReplicaIds();
        List<SerializedFileServer> replicaServers = buildReplicaServers(replicas);
        if (serverChecker.isActive(master)) {
            logger.debug("Master active, delete...");
            NodeServer server = nodeAllocator.findNodeServer(master);
            BlockDeleteServiceGrpc.BlockDeleteServiceStub stub =
                    requireStub(server);
            DeleteBlocksRequest request = DeleteBlocksRequest.newBuilder()
                    .setFileId(fileId)
                    .addAllServers(replicaServers)
                    .build();
            stub.deleteBlocks(request, DeleteResponseStreamObserver.getInstance());
        }
        logger.debug("Master with replicas delete complete.");
        repository.delete(location);
    }

    private static class DeleteResponseStreamObserver implements StreamObserver<DeleteBlocksResponse> {

        @Override
        public void onNext(DeleteBlocksResponse value) {
        }

        @Override
        public void onError(Throwable t) {
        }

        @Override
        public void onCompleted() {
        }

        public static DeleteResponseStreamObserver getInstance() {
            return SingletonHolder.INSTANCE;
        }

        private static final class SingletonHolder {
            static final DeleteResponseStreamObserver INSTANCE = new DeleteResponseStreamObserver();
        }
    }


    private List<SerializedFileServer> buildReplicaServers(String[] replicas) {
        List<NodeServer> nodeServers = new ArrayList<>();
        for (String replica : replicas) {
            if (serverChecker.isActive(replica)) {
                NodeServer server = nodeAllocator.findNodeServer(replica);
                nodeServers.add(server);
            }
        }
        return RequestServer.toSerialized(nodeServers);
    }

    private BlockDeleteServiceGrpc.BlockDeleteServiceStub requireStub(NodeServer server) {
        ManagedChannel channel = nodeChannelPool.getChannel(server);
        BlockDeleteServiceGrpc.BlockDeleteServiceStub stub =
                blockDeleteServiceStubPool.getStub(server.id());
        if (stub != null) {
            return stub;
        }
        stub = BlockDeleteServiceGrpc.newStub(channel);
        blockDeleteServiceStubPool.registerStub(server.id(), stub);
        return stub;
    }

}
