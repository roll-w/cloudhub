package org.huel.cloudhub.file.server.service.file;

import io.grpc.stub.StreamObserver;
import org.huel.cloudhub.file.fs.container.ContainerAllocator;
import org.huel.cloudhub.file.rpc.block.DeleteBlocksRequest;
import org.huel.cloudhub.file.rpc.block.DeleteBlocksResponse;
import org.huel.cloudhub.file.rpc.block.DeleteBlocksServiceGrpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author RollW
 */
@Service
public class BlockDeleteService extends DeleteBlocksServiceGrpc.DeleteBlocksServiceImplBase {
    private final ContainerAllocator containerAllocator;
    private final Logger logger = LoggerFactory.getLogger(BlockDeleteService.class);

    public BlockDeleteService(ContainerAllocator containerAllocator) {
        this.containerAllocator = containerAllocator;
    }

    @Override
    public void deleteBlocks(DeleteBlocksRequest request,
                             StreamObserver<DeleteBlocksResponse> responseObserver) {
        // TODO: 
    }
}
