package org.huel.cloudhub.file.server.file;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.huel.cloudhub.file.fs.block.BlockGroup;
import org.huel.cloudhub.file.fs.block.BlockMetaInfo;
import org.huel.cloudhub.file.fs.block.ContainerBlock;
import org.huel.cloudhub.file.fs.container.Container;
import org.huel.cloudhub.file.fs.container.ContainerGroup;
import org.huel.cloudhub.file.fs.container.ContainerProvider;
import org.huel.cloudhub.file.fs.container.ContainerReader;
import org.huel.cloudhub.file.rpc.block.BlockDownloadServiceGrpc;
import org.huel.cloudhub.file.rpc.block.DownloadBlockRequest;
import org.huel.cloudhub.file.rpc.block.DownloadBlockResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * @author RollW
 */
@Service
public class BlockDownloadService extends BlockDownloadServiceGrpc.BlockDownloadServiceImplBase {
    private final Logger logger = LoggerFactory.getLogger(BlockDownloadService.class);
    private final ContainerProvider containerProvider;

    public BlockDownloadService(ContainerProvider containerProvider) {
        this.containerProvider = containerProvider;
    }

    @Override
    public void downloadBlocks(DownloadBlockRequest request,
                               StreamObserver<DownloadBlockResponse> responseObserver) {

        final String fileId = request.getFileId();
        ContainerGroup containerGroup =
                containerProvider.findContainerGroupByFile(fileId);

        List<Container> containers =
                containerProvider.findContainersByFile(fileId);
        if (containers.isEmpty()) {
            responseObserver.onError(Status.NOT_FOUND.asException());
            responseObserver.onCompleted();
            return;
        }

        for (Container container : containers) {
            BlockMetaInfo blockMetaInfo = container.getBlockMetaInfoByFile(fileId);
            try (ContainerReader reader = new ContainerReader(container, containerProvider)) {
                List<ContainerBlock> containerBlocks = readBlocks(reader, blockMetaInfo);

            } catch (IOException e) {
                logger.error("an error occurred while reading the container", e);
            }
        }

    }

    private List<ContainerBlock> readBlocks(ContainerReader reader,
                                            BlockMetaInfo blockMetaInfo) throws IOException {
        List<ContainerBlock> containerBlocks = new LinkedList<>();
        for (BlockGroup blockGroup : blockMetaInfo.getBlockGroups()) {
            List<ContainerBlock> readBlocks =
                    reader.readBlocks(blockGroup.start(), blockGroup.end());
            containerBlocks.addAll(readBlocks);
        }

        return containerBlocks;
    }

}
