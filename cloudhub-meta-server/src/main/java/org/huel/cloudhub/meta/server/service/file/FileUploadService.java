package org.huel.cloudhub.meta.server.service.file;

import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.stub.StreamObserver;
import org.apache.commons.lang3.RandomStringUtils;
import org.huel.cloudhub.file.rpc.block.*;
import org.huel.cloudhub.meta.server.configuration.FileProperties;
import org.huel.cloudhub.meta.server.data.database.repository.FileStorageLocationRepository;
import org.huel.cloudhub.meta.server.data.entity.FileStorageLocation;
import org.huel.cloudhub.meta.server.service.node.*;
import org.huel.cloudhub.server.GrpcProperties;
import org.huel.cloudhub.server.StreamObserverWrapper;
import org.huel.cloudhub.server.rpc.heartbeat.SerializedFileServer;
import org.huel.cloudhub.util.math.Maths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author RollW
 */
@Service
public class FileUploadService {
    private final NodeAllocator nodeAllocator;
    private final FileProperties fileProperties;
    private final GrpcProperties grpcProperties;
    private final FileStorageLocationRepository storageLocationRepository;
    private final NodeChannelPool nodeChannelPool;
    private final ServerChecker serverChecker;

    public FileUploadService(HeartbeatService heartbeatService,
                             FileProperties fileProperties,
                             FileStorageLocationRepository storageLocationRepository,
                             GrpcProperties grpcProperties) {
        this.nodeAllocator = heartbeatService.getNodeAllocator();
        this.fileProperties = fileProperties;
        this.storageLocationRepository = storageLocationRepository;
        this.nodeChannelPool = new NodeChannelPool(grpcProperties);
        this.grpcProperties = grpcProperties;
        this.serverChecker = heartbeatService.getServerChecker();

        initial();
    }

    private final Logger logger = LoggerFactory.getLogger(FileUploadService.class);

    private boolean checkFileLocal(String hash) {
        FileStorageLocation location = storageLocationRepository.getByFileId(hash);
        if (location == null) {
            return false;
        }
        return checkServers(location.getServerList());
    }

    private boolean checkServers(List<String> serverIds) {
        if (serverIds == null) {
            return false;
        }

        for (String serverId : serverIds) {
            if (serverChecker.isActive(serverId)) {
                return true;
            }
        }
        // all servers goes down, means file not exists anymore, for now.
        // allowed to re-upload.
        return false;
    }

    public void uploadFile(InputStream inputStream) throws IOException {
        // TODO: add hash and file length param.
        logger.debug("Start calculation on the given input stream.");
        // 创建本地文件耗费IO时间。客户端上传时直接携带hash值和长度以便于计算
        Hasher crc32Hasher = Hashing.crc32().newHasher();
        Hasher sha256Hasher = Hashing.sha256().newHasher();
        ReopenableInputStream reopenableInputStream = convertInputStream(inputStream, crc32Hasher, sha256Hasher);
        final String hash = reopenableInputStream.getHash(sha256Hasher).toString();
        final String crc32 = reopenableInputStream.getHash(crc32Hasher).toString();
        if (checkFileLocal(hash)) {
            reopenableInputStream.close();
            logger.debug("file exists. file_id={}", hash);
            return;
        }

        reopenableInputStream.reopen();
        logger.debug("Start upload fileId={}", hash);

        final long maxBlocksValue = grpcProperties.getMaxRequestSizeBytes() >> 1;
        final int blockSizeInBytes = fileProperties.getBlockSizeInBytes();
        // calcs how many [UploadBlock]s a request can contain at most
        final int maxUploadBlockCount = (int) (maxBlocksValue / blockSizeInBytes);
        // calcs how many requests will be sent.
        final int requestCount = Maths.ceilDivideReturnsInt(reopenableInputStream.getLength(), maxBlocksValue);
        final long validBytes = reopenableInputStream.getLength() % blockSizeInBytes;
        logger.debug("Calc: length={};maxBlocksValue={};blockSizeInBytes={};maxUploadBlockCount={};requestCount={};validBytes={}",
                reopenableInputStream.getLength(), maxBlocksValue, blockSizeInBytes, maxUploadBlockCount, requestCount, validBytes);

        NodeServer master = nodeAllocator.allocateNode(hash);
        BlockUploadServiceGrpc.BlockUploadServiceStub stub =
                requiredBlockUploadServiceStub(master);
        UploadBlocksRequest firstRequest = buildFirstRequest(hash, crc32, validBytes,
                reopenableInputStream.getLength(), requestCount,
                List.of());// TODO: allocates replica servers

        UploadBlocksResponseStreamObserver responseStreamObserver =
                new UploadBlocksResponseStreamObserver(hash, reopenableInputStream,
                        master,
                        List.of(),
                        maxUploadBlockCount, blockSizeInBytes, requestCount);

        StreamObserver<UploadBlocksRequest> requestStreamObserver = stub.uploadBlocks(
                responseStreamObserver);
        responseStreamObserver.setRequestStreamObserver(requestStreamObserver);
        logger.debug("Start requesting for {}, server id={}......", hash, master.id());
        requestStreamObserver.onNext(firstRequest);
    }

    private BlockUploadServiceGrpc.BlockUploadServiceStub requiredBlockUploadServiceStub(NodeServer nodeServer) {
        ManagedChannel channel = nodeChannelPool.getChannel(nodeServer);

        BlockUploadServiceGrpc.BlockUploadServiceStub stub =
                BlockUploadServiceGrpc.newStub(channel);
        // TODO: caching stub
        return stub;
    }

    private UploadBlocksRequest buildFirstRequest(String hash, String crc32,
                                                  long validBytes, long length,
                                                  int requestCount,
                                                  List<SerializedFileServer> replicaServers) {
        UploadBlocksRequest.CheckMessage checkMessage = UploadBlocksRequest.CheckMessage
                .newBuilder()
                .setValidBytes(validBytes)
                .setRequestCount(requestCount)
                .setFileLength(length)
                .addAllReplicaHosts(replicaServers)
                .setCheckValue(crc32)
                .build();
        return UploadBlocksRequest.newBuilder()
                .setIdentity(hash)
                .setCheckMessage(checkMessage)
                .build();
    }

    private void updatesFileObjectLocation(String hash,
                                           String masterOrAlias,
                                           List<String> replicas) {
        FileStorageLocation location = storageLocationRepository.getByFileId(hash);
        if (location == null) {
            location = new FileStorageLocation(hash, masterOrAlias,
                    replicas.toArray(new String[0]), null);
            storageLocationRepository.insertOrUpdate(location);
            return;
        }
        String[] alias = appendOrCreate(location.getAliasIds(), masterOrAlias);
        String[] newReplicas = appendOrCreate(location.getReplicaIds(),
                replicas.toArray(new String[0]));
        location.setReplicaIds(newReplicas);
        location.setAliasIds(alias);
        storageLocationRepository.insertOrUpdate(location);
    }

    private String[] appendOrCreate(String[] original, String... appends) {
        if (original == null) {
            return appends;
        }
        List<String> create = new ArrayList<>();
        create.addAll(Arrays.asList(original));
        create.addAll(Arrays.asList(appends));
        return create.toArray(new String[0]);
    }

    private class UploadBlocksResponseStreamObserver implements StreamObserver<UploadBlocksResponse> {
        private final NodeServer master;
        private final List<String> replicaIds;

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
                                           NodeServer master,
                                           List<NodeServer> replicas,
                                           int maxUploadBlockCount,
                                           int blockSizeInBytes,
                                           int requestCount) {
            this.stream = stream;
            this.fileId = fileId;
            this.master = master;
            this.replicaIds = replicas.stream().map(NodeServer::id).toList();
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
                    logger.debug("File exists.");
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

            logger.debug("check block count: {}", value.getBlockCount());
        }

        @Override
        public void onError(Throwable t) {
            // received error needs retry.
            // sets a limit for retry times.
            try {
                stream.close();
            } catch (IOException ignored) {
            }
            logger.error("Error receive upload blocks response", t);
        }

        @Override
        public void onCompleted() {
            try {
                stream.close();
            } catch (IOException ignored) {
            }
            // success upload.
            updatesFileObjectLocation(fileId, master.id(), replicaIds);
            logger.debug("Upload file complete.");
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

    private ReopenableInputStream convertInputStream(InputStream inputStream, Hasher... hashers) throws IOException {
        File tempDir = new File(fileProperties.getTempFilePath());
        File tempFile = new File(tempDir,
                RandomStringUtils.randomAlphanumeric(20));
        return new ReopenableInputStream(inputStream, tempFile, hashers);
    }

    private void initial() {
        File tempDir = new File(fileProperties.getTempFilePath());
        if (tempDir.exists()) {
            tempDir.delete();
        }
        tempDir.mkdirs();
    }

    // finds replica servers.
    private List<NodeServer> allocatesReplicaServers(String hash, int replicas) {
        List<NodeServer> servers = new ArrayList<>();
        for (int i = 0; i < replicas; i++) {
            NodeServer server =
                    nodeAllocator.allocateNode(hash + "-" + i);
            servers.add(server);
        }
        return servers;
    }

    /**
     * Calculate the number of replicas required.
     *
     * @return replicas count
     */
    private int calcReplicas() {
        int activeServerSize = serverChecker.getActiveServerCount();
        return Math.min(activeServerSize, 3);
        //return (activeServerSize / 3) * 3 - (activeServerSize / 3 - 1);
    }
}
