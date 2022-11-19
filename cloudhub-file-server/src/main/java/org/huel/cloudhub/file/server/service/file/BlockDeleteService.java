package org.huel.cloudhub.file.server.service.file;

import io.grpc.Context;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.huel.cloudhub.file.fs.block.BlockGroupsInfo;
import org.huel.cloudhub.file.fs.block.BlockMetaInfo;
import org.huel.cloudhub.file.fs.container.Container;
import org.huel.cloudhub.file.fs.container.ContainerAllocator;
import org.huel.cloudhub.file.fs.container.ContainerDeleter;
import org.huel.cloudhub.file.fs.container.ContainerFinder;
import org.huel.cloudhub.file.fs.meta.MetaException;
import org.huel.cloudhub.file.rpc.block.BlockDeleteServiceGrpc;
import org.huel.cloudhub.file.rpc.block.DeleteBlocksRequest;
import org.huel.cloudhub.file.rpc.block.DeleteBlocksResponse;
import org.huel.cloudhub.file.server.service.SourceServerGetter;
import org.huel.cloudhub.file.server.service.replica.ReplicaService;
import org.huel.cloudhub.file.server.service.replica.ReplicaSynchroPart;
import org.huel.cloudhub.server.rpc.heartbeat.SerializedFileServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author RollW
 */
@Service
public class BlockDeleteService extends BlockDeleteServiceGrpc.BlockDeleteServiceImplBase {
    private final ContainerAllocator containerAllocator;
    private final ContainerFinder containerFinder;
    private final ContainerDeleter containerDeleter;
    private final ReplicaService replicaService;
    private final Logger logger = LoggerFactory.getLogger(BlockDeleteService.class);
    private final SourceServerGetter.ServerInfo serverInfo;

    public BlockDeleteService(ContainerAllocator containerAllocator,
                              ContainerFinder containerFinder,
                              ContainerDeleter containerDeleter,
                              ReplicaService replicaService, SourceServerGetter sourceServerGetter) {
        this.containerAllocator = containerAllocator;
        this.containerFinder = containerFinder;
        this.containerDeleter = containerDeleter;
        this.replicaService = replicaService;
        this.serverInfo = sourceServerGetter.getLocalServer();
    }

    @Override
    public void deleteBlocks(DeleteBlocksRequest request,
                             StreamObserver<DeleteBlocksResponse> responseObserver) {
        final String fileId = request.getFileId();
        List<SerializedFileServer> replicaServers = request.getServersList();
        String source = request.getSource();
        if (source.isEmpty()) {
            source = serverInfo.id();
        }

        List<Container> containers =
                containerFinder.findContainersByFile(fileId, ContainerFinder.LOCAL);
        Context context = Context.current().fork();
        List<ReplicaSynchroPart> parts = new ArrayList<>();
        for (Container container : containers) {
            try {
                boolean containerDelete = releaseBlockOccupation(
                        fileId, source, container, replicaServers);
                if (containerDelete) {
                    continue;
                }
                ReplicaSynchroPart part =
                        new ReplicaSynchroPart(container, BlockGroupsInfo.EMPTY, 0);
                parts.add(part);
            } catch (IOException | MetaException e) {
                responseObserver.onError(Status.INTERNAL.asException());
                logger.error("Delete blocks error: ", e);
                return;
            }
        }
        responseObserver.onNext(DeleteBlocksResponse.newBuilder().build());
        responseObserver.onCompleted();
        context.run(() -> sendReplicaSynchroRequest(parts, replicaServers));
    }

    private boolean releaseBlockOccupation(String fileId, String source,
                                           Container container,
                                           List<SerializedFileServer> servers) throws IOException, MetaException {
        List<BlockMetaInfo> newInfos = new ArrayList<>(container.getBlockMetaInfos());
        if (newInfos.isEmpty()) {
            return false;
        }
        BlockMetaInfo blockMetaInfo = container.getBlockMetaInfoByFile(fileId);
        newInfos.remove(blockMetaInfo);
        container.setBlockMetaInfos(newInfos);
        Context context = Context.current().fork();
        if (container.getUsedBlocksCount() == 0) {
            containerDeleter.deleteContainer(container);
            context.run(() ->
                    startReplicaDeleteRequest(container, source, servers));
            return true;
        }
        containerAllocator.updatesContainerMetadata(container);
        return false;
    }

    private void startReplicaDeleteRequest(Container container, String source,
                                           List<SerializedFileServer> servers) {
        servers.forEach(server ->
                replicaService.requestReplicaDelete(container, source, server));
    }

    private void sendReplicaSynchroRequest(List<ReplicaSynchroPart> parts,
                                           List<SerializedFileServer> servers) {
        servers.forEach(server -> replicaService.requestReplicasSynchro(parts, server));
    }
}
