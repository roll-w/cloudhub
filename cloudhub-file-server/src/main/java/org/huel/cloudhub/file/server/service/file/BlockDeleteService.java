package org.huel.cloudhub.file.server.service.file;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.huel.cloudhub.file.fs.container.ContainerDeleter;
import org.huel.cloudhub.file.fs.block.BlockMetaInfo;
import org.huel.cloudhub.file.fs.container.Container;
import org.huel.cloudhub.file.fs.container.ContainerAllocator;
import org.huel.cloudhub.file.fs.container.ContainerFinder;
import org.huel.cloudhub.file.fs.meta.MetaException;
import org.huel.cloudhub.file.rpc.block.DeleteBlocksRequest;
import org.huel.cloudhub.file.rpc.block.DeleteBlocksResponse;
import org.huel.cloudhub.file.rpc.block.DeleteBlocksServiceGrpc;
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
public class BlockDeleteService extends DeleteBlocksServiceGrpc.DeleteBlocksServiceImplBase {
    private final ContainerAllocator containerAllocator;
    private final ContainerFinder containerFinder;
    private final ContainerDeleter containerDeleter;
    private final Logger logger = LoggerFactory.getLogger(BlockDeleteService.class);

    public BlockDeleteService(ContainerAllocator containerAllocator,
                              ContainerFinder containerFinder,
                              ContainerDeleter containerDeleter) {
        this.containerAllocator = containerAllocator;
        this.containerFinder = containerFinder;
        this.containerDeleter = containerDeleter;
    }

    @Override
    public void deleteBlocks(DeleteBlocksRequest request,
                             StreamObserver<DeleteBlocksResponse> responseObserver) {
        final String fileId = request.getFileId();
        List<Container> containers =
                containerFinder.findContainersByFile(fileId, ContainerFinder.LOCAL);
        for (Container container : containers) {
            try {
                releaseBlockOccupation(fileId, container);
            } catch (IOException | MetaException e) {
                responseObserver.onError(Status.INTERNAL.asException());
                logger.error("delete blocks error: ", e);
                return;
            }
        }
        responseObserver.onNext(DeleteBlocksResponse.newBuilder().build());
        responseObserver.onCompleted();
    }

    private void releaseBlockOccupation(String fileId, Container container) throws IOException, MetaException {
        List<BlockMetaInfo> newInfos = new ArrayList<>(container.getBlockMetaInfos());
        if (newInfos.isEmpty()) {
            return;
        }
        BlockMetaInfo blockMetaInfo = container.getBlockMetaInfoByFile(fileId);
        newInfos.remove(blockMetaInfo);
        container.setBlockMetaInfos(newInfos);

        if (container.getUsedBlocksCount() == 0) {
            containerDeleter.deleteContainer(container);
        }
        containerAllocator.updatesContainerMetadata(container);
    }
}
