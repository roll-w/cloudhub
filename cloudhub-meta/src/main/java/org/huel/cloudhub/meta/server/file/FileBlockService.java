package org.huel.cloudhub.meta.server.file;

import com.google.common.hash.Hashing;
import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import org.huel.cloudhub.file.rpc.block.*;
import org.huel.cloudhub.meta.server.node.HeartbeatService;
import org.huel.cloudhub.meta.server.node.NodeChannelPool;
import org.huel.cloudhub.server.file.FileProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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

    public void sendBlock(byte[] data) {
        String hash =
                Hashing.sha256().hashBytes(data).toString();
        ManagedChannel channel = nodeChannelPool.getChannel(heartbeatService.randomServer());
        BlockUploadServiceGrpc.BlockUploadServiceBlockingStub stub =
                BlockUploadServiceGrpc.newBlockingStub(channel);
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
                .setHash(0)
                .setValidBytes(validBytes)
                .setCheckValue("0")
                .addAllBlock(uploadBlocks)
                .setCompressType(BlockRequestCompressType.NONE)
                .build();
        UploadBlocksResponse response =
                stub.uploadBlocks(request);
        logger.info("receive upload response, version: {}", response.getVersion());
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
