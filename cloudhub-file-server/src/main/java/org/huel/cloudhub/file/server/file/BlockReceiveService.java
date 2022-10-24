package org.huel.cloudhub.file.server.file;

import io.grpc.stub.StreamObserver;
import org.huel.cloudhub.file.fs.block.Block;
import org.huel.cloudhub.file.fs.block.BlockMetaInfo;
import org.huel.cloudhub.file.fs.block.Blocks;
import org.huel.cloudhub.file.fs.container.Container;
import org.huel.cloudhub.file.fs.container.ContainerWriter;
import org.huel.cloudhub.file.fs.meta.MetaException;
import org.huel.cloudhub.file.rpc.block.BlockUploadServiceGrpc;
import org.huel.cloudhub.file.rpc.block.UploadBlock;
import org.huel.cloudhub.file.rpc.block.UploadBlocksRequest;
import org.huel.cloudhub.file.rpc.block.UploadBlocksResponse;
import org.huel.cloudhub.server.file.FileProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import space.lingu.NonNull;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * @author RollW
 */
@Service
public class BlockReceiveService extends BlockUploadServiceGrpc.BlockUploadServiceImplBase {
    private final Logger logger = LoggerFactory.getLogger(BlockReceiveService.class);
    private final FileProperties fileProperties;
    private final ContainerService containerService;

    public BlockReceiveService(FileProperties fileProperties,
                               ContainerService containerService) {
        this.fileProperties = fileProperties;
        this.containerService = containerService;
    }

    @Override
    public void uploadBlocks(UploadBlocksRequest request,
                             StreamObserver<UploadBlocksResponse> responseObserver) {
        int count = request.getBlockCount();
        logger.info("receive blocks. count=[{}];id={};", count, request.getIdentity());
        List<UploadBlock> blocks = request.getBlockList();
        if (containerService.dataExists(request.getIdentity())) {
            logger.info("file exists, id={}", request.getIdentity());
            responseObserver.onNext(UploadBlocksResponse.newBuilder()
                    .setVersion(0)
                    .build());
            responseObserver.onCompleted();
            return;
        }

        Container container =
                containerService.allocateContainer(request.getIdentity());
        try (ContainerWriter writer = new ContainerWriter(container, containerService)) {
            container = writeAndPushMeta(writer,
                    request.getIdentity(),
                    request.getValidBytes(), blocks);
        } catch (IOException | MetaException e) {
            responseObserver.onError(e);
            logger.error("container writer error: ", e);
            return;
        }
        logger.info("write all blocks.");
        // container allocate结束后，写入并重新计算Crc
        // BlockRequest发送时应同时包含需要复制副本的主机地址

        // ?: 复制原文件后更新
        responseObserver.onNext(UploadBlocksResponse.newBuilder()
                .setVersion(container.getIdentity().serial())
                .build());
        responseObserver.onCompleted();
    }

    @NonNull
    private Container writeAndPushMeta(ContainerWriter writer,
                                       String fileId,
                                       int validBytes,
                                       List<UploadBlock> uploadBlocks)
            throws IOException, MetaException {
        List<Block> blockList = new LinkedList<>();
        final int size = uploadBlocks.size();
        for (int i = 0; i < size; i++) {
            UploadBlock uploadBlock = uploadBlocks.get(i);
            byte[] chunk = uploadBlock.getData().toByteArray();
            Block block = new Block(
                    uploadBlock.getData().toByteArray(),
                    i == size - 1 ? validBytes : chunk.length);
            blockList.add(block);
        }
        Blocks blocks = new Blocks(blockList, fileId, validBytes);
        List<BlockMetaInfo> infos = writer.writeBlocks(blocks);

        return writer.requireUpdate();
    }


}
