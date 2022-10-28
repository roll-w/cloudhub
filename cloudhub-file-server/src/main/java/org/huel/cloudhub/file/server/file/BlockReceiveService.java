package org.huel.cloudhub.file.server.file;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.huel.cloudhub.file.fs.block.Block;
import org.huel.cloudhub.file.fs.block.BlockMetaInfo;
import org.huel.cloudhub.file.fs.block.Blocks;
import org.huel.cloudhub.file.fs.container.Container;
import org.huel.cloudhub.file.fs.container.ContainerWriter;
import org.huel.cloudhub.file.fs.meta.MetaException;
import org.huel.cloudhub.file.rpc.block.*;
import org.huel.cloudhub.server.StreamObserverWrapper;
import org.huel.cloudhub.server.file.FileProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import space.lingu.NonNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
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
    public StreamObserver<UploadBlocksRequest> uploadBlocks(StreamObserver<UploadBlocksResponse> responseObserver) {
        return new UploadBlocksRequestObserver(responseObserver);
    }

    private class UploadBlocksRequestObserver implements StreamObserver<UploadBlocksRequest> {
        private final StreamObserverWrapper<UploadBlocksResponse> responseObserver;
        private final List<IndexedBlockRequest> indexedBlockRequests =
                new ArrayList<>();

        // TODO: or allocates to a temp file?
        //  may produce out of memory error if the file too large.

        // add a staging dir, and puts blocks in the area.
        // when receive all requests,
        // reads all data into the container step by step after.

        private String fileId;
        private int indexCount = -1;
        private long validBytes;

        UploadBlocksRequestObserver(StreamObserver<UploadBlocksResponse> responseObserver) {
            this.responseObserver = new StreamObserverWrapper<>(responseObserver);
        }

        private void checkExistsWithClose(String fileId) {
            boolean exists = containerService.dataExists(fileId);
            UploadBlocksResponse response = UploadBlocksResponse.newBuilder()
                    .setFileExists(exists)
                    .build();
            responseObserver.onNext(response);
            if (!exists) {
                return;
            }
            logger.info("file exists, id={}", fileId);
            responseObserver.onCompleted();
        }

        private void saveCheckMessageInfo(UploadBlocksRequest.CheckMessage checkMessage) {
            indexCount = checkMessage.getRequestCount();
            validBytes = checkMessage.getValidBytes();
            logger.info("receive upload message. count={};validBytes={};",
                    indexCount, validBytes);
        }

        @Override
        public void onNext(UploadBlocksRequest request) {
            if (responseObserver.isClose()) {
                return;
            }
            if (request.getUploadCase() == UploadBlocksRequest.UploadCase.UPLOAD_NOT_SET) {
                responseObserver.onError(Status.INVALID_ARGUMENT.asException());
                return;
            }
            if (fileId != null && !fileId.equals(request.getIdentity())) {
                Exception exception =
                        new IllegalStateException("The ids of the files before and after are different.");
                responseObserver.onError(exception);
                logger.error("error receive blocks.");
                return;
            }
            fileId = request.getIdentity();
            checkExistsWithClose(fileId);

            if (request.getUploadCase() == UploadBlocksRequest.UploadCase.CHECK_MESSAGE) {
                saveCheckMessageInfo(request.getCheckMessage());
                return;
            }

            UploadBlocksInfo uploadBlocksInfo = request.getUploadBlocks();
            final int count = uploadBlocksInfo.getBlocksCount();
            logger.info("receive blocks. index={};count=[{}];id={};",
                    uploadBlocksInfo.getIndex(), count, request.getIdentity());
            indexedBlockRequests.add(new IndexedBlockRequest(
                    uploadBlocksInfo.getBlocksList(), uploadBlocksInfo.getIndex()));
        }

        @Override
        public void onError(Throwable t) {
            logger.error("receive blocks error.", t);
        }

        @Override
        public void onCompleted() {
            if (indexCount <= 0 && responseObserver.isOpen()) {
                logger.info("invalid index count {} in uploading {}", indexCount, fileId);
                responseObserver.onError(Status.INVALID_ARGUMENT.asException());
                return;
            }
            if (indexCount != indexedBlockRequests.size() && responseObserver.isOpen()) {
                logger.info("data may have been lost in uploading {}. given: {}, receive: {}",
                        fileId, indexCount, indexedBlockRequests.size());
                responseObserver.onError(Status.DATA_LOSS.asException());
                return;
            }
            if (responseObserver.isClose()) {
                return;
            }

            List<UploadBlockData> uploadBlocks =
                    convertsUploadBlocks(indexedBlockRequests);
            Container container =
                    containerService.allocateContainer(fileId);

            try (ContainerWriter writer = new ContainerWriter(container, containerService)) {
                WriteInfo writeInfo = writeAndPushMeta(writer,
                        fileId, validBytes, uploadBlocks);
                responseObserver.onNext(UploadBlocksResponse.newBuilder()
                        .setBlockCount(writeInfo.writeBlockInfos.size())// TODO:
                        .build());
                responseObserver.onCompleted();
            } catch (IOException | MetaException e) {
                responseObserver.onError(e);
                e.printStackTrace();
                logger.error("container writer error: ", e);
            }
        }
    }

    @NonNull
    private WriteInfo writeAndPushMeta(ContainerWriter writer,
                                       String fileId,
                                       long validBytes,
                                       List<UploadBlockData> uploadBlocks)
            throws IOException, MetaException {
        List<Block> blockList = new LinkedList<>();
        final int size = uploadBlocks.size();
        for (int i = 0; i < size; i++) {
            UploadBlockData uploadBlock = uploadBlocks.get(i);
            byte[] chunk = uploadBlock.getData().toByteArray();
            Block block = new Block(
                    uploadBlock.getData().toByteArray(),
                    i == size - 1 ? validBytes : chunk.length);
            blockList.add(block);
        }
        Blocks blocks = new Blocks(blockList, fileId, validBytes);

        List<BlockMetaInfo> infos = writer.writeBlocks(blocks);
        Container container = writer.requireUpdate();
        return new WriteInfo(container, infos);
    }

    private record WriteInfo(
            Container container,
            List<BlockMetaInfo> writeBlockInfos
    ) {
    }

    private record IndexedBlockRequest(
            List<UploadBlockData> uploadBlocks,
            int index
    ) {
    }

    private static List<UploadBlockData> convertsUploadBlocks(
            List<IndexedBlockRequest> indexedBlockRequests) {
        return indexedBlockRequests.stream()
                .sorted(Comparator.comparingInt(IndexedBlockRequest::index))
                .collect(LinkedList::new,
                        (blocks, indexedBlockRequest) ->
                                blocks.addAll(indexedBlockRequest.uploadBlocks),
                        LinkedList::addAll);
    }
}
