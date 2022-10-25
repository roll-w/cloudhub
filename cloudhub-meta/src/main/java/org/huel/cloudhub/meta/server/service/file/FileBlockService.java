package org.huel.cloudhub.meta.server.service.file;

import com.google.common.hash.Hashing;
import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.stub.StreamObserver;
import org.huel.cloudhub.file.rpc.block.*;
import org.huel.cloudhub.meta.server.service.node.HeartbeatService;
import org.huel.cloudhub.meta.server.service.node.NodeChannelPool;
import org.huel.cloudhub.server.file.FileProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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
        byte[][] fragments = fragmentBytes(data);
        final int bytesLength = fragments.length;
        final int validBytes = fragments[bytesLength - 1].length;
        List<UploadBlock> uploadBlocks = new ArrayList<>();

        for (byte[] fragment : fragments) {
            UploadBlock uploadBlock = UploadBlock
                    .newBuilder()
                    .setData(ByteString.copyFrom(fragment)).build();
            uploadBlocks.add(uploadBlock);
        }
        logger.info("sending request......");
        UploadBlocksRequest request = UploadBlocksRequest.newBuilder()
                .setIdentity(hash)
                .setIndex(0)
                .setIndexCount(1)
                .setValidBytes(validBytes)
                .setCheckValue("0")
                .addAllBlock(uploadBlocks)
                .setCompressType(BlockRequestCompressType.NONE)
                .build();
        StreamObserver<UploadBlocksRequest> requestStreamObserver = stub.uploadBlocks(
                new UploadBlocksResponseStreamObserver());
        requestStreamObserver.onNext(request);
        requestStreamObserver.onCompleted();
    }


    private class UploadBlocksResponseStreamObserver implements StreamObserver<UploadBlocksResponse> {
        @Override
        public void onNext(UploadBlocksResponse value) {
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
