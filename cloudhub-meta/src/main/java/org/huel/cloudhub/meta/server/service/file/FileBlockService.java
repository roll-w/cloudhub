package org.huel.cloudhub.meta.server.service.file;

import com.google.common.hash.Funnels;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import com.google.common.io.ByteStreams;
import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.stub.StreamObserver;
import org.apache.commons.lang3.RandomStringUtils;
import org.huel.cloudhub.file.rpc.block.*;
import org.huel.cloudhub.meta.server.service.node.HeartbeatService;
import org.huel.cloudhub.meta.server.service.node.NodeChannelPool;
import org.huel.cloudhub.server.StreamObserverWrapper;
import org.huel.cloudhub.server.file.FileProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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

    @SuppressWarnings("all")
    private static String hashStream(InputStream stream) throws IOException {
        Hasher hasher = Hashing.sha256().newHasher();
        ByteStreams.copy(stream, Funnels.asOutputStream(hasher));
        return hasher.hash().toString();
    }

    public void sendBlock(InputStream inputStream) throws IOException {
        // TODO: add hash and file length param.

        // 创建本地文件耗费IO时间。客户端上传时直接携带hash值和长度以便于计算
        ReopenableInputStream reopenableInputStream = convertInputStream(inputStream);
        final String hash = hashStream(reopenableInputStream);
        reopenableInputStream.reopen();

        logger.info("start upload fileId={}", hash);

        final long maxBlocksValue = fileProperties.getMaxRequestSizeBytes() >> 1;
        final int blockSizeInBytes = fileProperties.getBlockSizeInBytes();
        // calcs how many [UploadBlock]s a request can contain at most
        final int maxUploadBlockCount = (int) (maxBlocksValue / blockSizeInBytes);
        // calcs how many requests will be sent.
        final int requestCount = (int) Math.ceil(1.0d * reopenableInputStream.getLength() / maxBlocksValue);
        final long validBytes = maxBlocksValue - ((long) requestCount * maxBlocksValue -
                reopenableInputStream.getLength());
        logger.info("calc: length={};maxBlocksValue={};blockSizeInBytes={};maxUploadBlockCount={};requestCount={};validBytes={}",
                reopenableInputStream.getLength(), maxBlocksValue, blockSizeInBytes, maxUploadBlockCount, requestCount, validBytes);

        // TODO: verify the file exists first using data in the meta-server
        BlockUploadServiceGrpc.BlockUploadServiceStub stub =
                requiredBlockUploadServiceStub(hash);
        UploadBlocksRequest.CheckMessage checkMessage = UploadBlocksRequest.CheckMessage
                .newBuilder()
                .setValidBytes(validBytes)
                .setRequestCount(requestCount)
                .setCompressType(BlockRequestCompressType.NONE)
                .setCheckValue("0")// TODO: calc CRC32
                .build();
        UploadBlocksRequest firstRequest = UploadBlocksRequest.newBuilder()
                .setIdentity(hash)
                .setCheckMessage(checkMessage)
                .build();
        UploadBlocksResponseStreamObserver responseStreamObserver =
                new UploadBlocksResponseStreamObserver(hash, reopenableInputStream,
                        maxUploadBlockCount, blockSizeInBytes, requestCount);

        StreamObserver<UploadBlocksRequest> requestStreamObserver = stub.uploadBlocks(
                responseStreamObserver);
        responseStreamObserver.setRequestStreamObserver(requestStreamObserver);
        logger.info("start requesting......");
        requestStreamObserver.onNext(firstRequest);
    }

    private BlockUploadServiceGrpc.BlockUploadServiceStub requiredBlockUploadServiceStub(String hash) {
        ManagedChannel channel = nodeChannelPool.getChannel(
                heartbeatService.randomServer());
        // TODO: replace with {NodeAllocator}

        BlockUploadServiceGrpc.BlockUploadServiceStub stub =
                BlockUploadServiceGrpc.newStub(channel);
        // TODO: caching stub
        return stub;
    }

    private class UploadBlocksResponseStreamObserver implements StreamObserver<UploadBlocksResponse> {
        private final ReopenableInputStream stream;
        private final String fileId;
        private final int maxUploadBlockCount, blockSizeInBytes,
                requestCount;
        private StreamObserverWrapper<UploadBlocksRequest>
                requestStreamObserver;
        private final BufferedStreamIterator iterator;
        private final AtomicInteger requestIndex = new AtomicInteger(0);

        public void setRequestStreamObserver(StreamObserver<UploadBlocksRequest> requestStreamObserver) {
            this.requestStreamObserver = new StreamObserverWrapper<>(requestStreamObserver);
        }

        UploadBlocksResponseStreamObserver(String fileId,
                                           ReopenableInputStream stream,
                                           int maxUploadBlockCount,
                                           int blockSizeInBytes,
                                           int requestCount) {
            this.stream = stream;
            this.fileId = fileId;
            this.maxUploadBlockCount = maxUploadBlockCount;
            this.blockSizeInBytes = blockSizeInBytes;
            this.requestCount = requestCount;
            iterator = new BufferedStreamIterator(this.stream,
                    this.blockSizeInBytes);
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
                    logger.info("file exists.");
                    requestStreamObserver.onCompleted();
                    return;
                }
                try {
                    sendData();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return;
            }

            logger.info("check block count: {}", value.getBlockCount());
        }

        @Override
        public void onError(Throwable t) {
            // received error needs retry.
            // sets a limit for retry times.
            try {
                stream.close();
            } catch (IOException e) {
            }
            logger.error("error receive upload blocks response", t);
        }

        @Override
        public void onCompleted() {
            try {
                stream.close();
            } catch (IOException e) {
            }
            logger.info("upload block complete.");
        }

        private void sendData() throws IOException {
            List<UploadBlockData> blocks = new ArrayList<>();
            for (int i = 0; i < maxUploadBlockCount; i++) {
                BufferedStreamIterator.Buffer buffer =
                        iterator.nextBuffer();
                UploadBlockData uploadBlockData = UploadBlockData.newBuilder()
                        .setData(ByteString.copyFrom(
                                buffer.getData(), 0, (int) buffer.getSize()))
                        .build();
                blocks.add(uploadBlockData);
                if (buffer.notFilled()) {
                    break;
                }
            }

            UploadBlocksRequest request = buildUploadBlocksRequest(fileId, blocks,
                    requestIndex.get());
            requestStreamObserver.onNext(request);
            if (requestIndex.get() + 1 >= requestCount) {
                requestStreamObserver.onCompleted();
            }
            requestIndex.incrementAndGet();
        }
    }

    private UploadBlocksRequest buildUploadBlocksRequest(String fileId,
                                                         List<UploadBlockData> uploadBlocks,
                                                         int index) {
        UploadBlocksInfo blocks = UploadBlocksInfo.newBuilder()
                .addAllBlocks(uploadBlocks)
                .setIndex(index)
                .build();
        return UploadBlocksRequest.newBuilder()
                .setIdentity(fileId)
                .setUploadBlocks(blocks)
                .build();
    }

    private ReopenableInputStream convertInputStream(InputStream inputStream) throws IOException {
        File tempDir = new File(fileProperties.getTempFilePath());
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }
        File tempFile = new File(tempDir,
                RandomStringUtils.randomAlphanumeric(20));
        return new ReopenableInputStream(inputStream, tempFile);
    }
}
