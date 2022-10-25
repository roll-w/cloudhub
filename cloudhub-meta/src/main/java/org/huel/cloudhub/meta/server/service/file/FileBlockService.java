package org.huel.cloudhub.meta.server.service.file;

import com.google.common.hash.Hashing;
import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.stub.StreamObserver;
import org.huel.cloudhub.file.rpc.block.*;
import org.huel.cloudhub.meta.server.service.node.HeartbeatService;
import org.huel.cloudhub.meta.server.service.node.NodeChannelPool;
import org.huel.cloudhub.server.StreamObserverWrapper;
import org.huel.cloudhub.server.file.FileProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author RollW
 */
@Service
public class FileBlockService {
    private final HeartbeatService heartbeatService;
    private final FileProperties fileProperties;
    private final NodeChannelPool nodeChannelPool;

    public FileBlockService(HeartbeatService heartbeatService,
                            FileProperties fileProperties) {
        this.heartbeatService = heartbeatService;
        this.fileProperties = fileProperties;
        this.nodeChannelPool = new NodeChannelPool();
    }

    private final Logger logger = LoggerFactory.getLogger(FileBlockService.class);


    // TODO: 发送时，对大小超过某个值的文件
    //  分块后发送到不同的file server上，以使得下载可以并行。

    public void sendBlock(InputStream inputStream) {
        // TODO
        final long maxBlocksValue = fileProperties.getMaxRequestSizeBytes() >> 1;

    }

    public void sendBlock(byte[] data) {
        String hash =
                Hashing.sha256().hashBytes(data).toString();
        ManagedChannel channel = nodeChannelPool.getChannel(
                heartbeatService.randomServer());

        BlockUploadServiceGrpc.BlockUploadServiceStub stub =
                BlockUploadServiceGrpc.newStub(channel);
        UploadBlocksRequest firstRequest = UploadBlocksRequest.newBuilder()
                .setIdentity(hash)
                .build();
        UploadBlocksResponseStreamObserver responseStreamObserver =
                new UploadBlocksResponseStreamObserver(hash, data);
        StreamObserver<UploadBlocksRequest> requestStreamObserver = stub.uploadBlocks(
                responseStreamObserver);
        responseStreamObserver.setRequestStreamObserver(requestStreamObserver);
        requestStreamObserver.onNext(firstRequest);

    }


    private class UploadBlocksResponseStreamObserver implements StreamObserver<UploadBlocksResponse> {
        private final byte[] data;
        private final String fileId;

        private StreamObserverWrapper<UploadBlocksRequest>
                requestStreamObserver;

        public void setRequestStreamObserver(StreamObserver<UploadBlocksRequest> requestStreamObserver) {
            this.requestStreamObserver = new StreamObserverWrapper<>(requestStreamObserver);
        }

        UploadBlocksResponseStreamObserver(String fileId, byte[] data) {
            this.data = data;
            this.fileId = fileId;
        }

        UploadBlocksResponseStreamObserver(String fileId, InputStream stream) throws IOException {
            this.data = stream.readAllBytes();
            this.fileId = fileId;
            // TODO;
        }

        @Override
        public void onNext(UploadBlocksResponse value) {
            if (requestStreamObserver.isClose()) {
                return;
            }
            if (value.getBlockResponseCase() ==
                    UploadBlocksResponse.BlockResponseCase.BLOCKRESPONSE_NOT_SET) {
                throw new IllegalStateException("Not set on block response");
            }
            if (value.getBlockResponseCase() ==
                    UploadBlocksResponse.BlockResponseCase.FILE_EXISTS) {
                if (value.getFileExists()) {
                    requestStreamObserver.onCompleted();
                    return;
                }
                sendData();
                return;
            }

            logger.info("check block count: {}", value.getBlockCount());
        }

        @Override
        public void onError(Throwable t) {
            // received error needs retry.
            // sets a limit for retry times.
            logger.error("error receive upload blocks response", t);
        }

        @Override
        public void onCompleted() {
            logger.info("upload block complete.");
        }

        private void sendData() {
            byte[][] fragments = fragmentBytes(data);
            final int bytesLength = fragments.length;
            final int validBytes = fragments[bytesLength - 1].length;
            List<UploadBlock> uploadBlockList = new ArrayList<>();

            for (byte[] fragment : fragments) {
                UploadBlock uploadBlock = UploadBlock
                        .newBuilder()
                        .setData(ByteString.copyFrom(fragment)).build();
                uploadBlockList.add(uploadBlock);
            }
            logger.info("sending request......");
            UploadBlocks uploadBlocks = UploadBlocks.newBuilder()
                    .addAllBlocks(uploadBlockList)
                    .setCheckValue("0")
                    .setIndex(0)
                    .setValidBytes(validBytes)
                    .setIndexCount(1)
                    .build();
            UploadBlocksRequest request = UploadBlocksRequest.newBuilder()
                    .setIdentity(fileId)
                    .setUploadBlocks(uploadBlocks)
                    .build();

            requestStreamObserver.onNext(request);
            requestStreamObserver.onCompleted();
            // TODO: stream
        }
    }

    /**
     * 分片
     */
    private byte[][] fragmentBytes(byte[] bytes) {
        int blockSizeBytes = fileProperties.getBlockSize() * 1024;
        int length = (int) Math.ceil(bytes.length / 1024.0 /
                fileProperties.getBlockSize());
        byte[][] byteContainer = new byte[length][];
        for (int i = 0; i < length - 1; i++) {
            byteContainer[i] = Arrays.copyOfRange(bytes,
                    i * blockSizeBytes,
                    (i + 1) * blockSizeBytes);
        }
        byteContainer[length - 1] = Arrays.copyOfRange(bytes,
                (length - 1) * blockSizeBytes,
                bytes.length);

        logger.info("fragments count={}, bytes length={}", length, bytes.length);
        return byteContainer;
    }
}
