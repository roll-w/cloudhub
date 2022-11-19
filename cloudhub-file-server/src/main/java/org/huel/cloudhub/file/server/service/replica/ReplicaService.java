package org.huel.cloudhub.file.server.service.replica;

import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.huel.cloudhub.file.fs.LockException;
import org.huel.cloudhub.file.fs.block.ContainerBlock;
import org.huel.cloudhub.file.fs.container.*;
import org.huel.cloudhub.file.fs.meta.SerializedContainerBlockMeta;
import org.huel.cloudhub.file.rpc.replica.*;
import org.huel.cloudhub.file.server.service.SourceServerGetter;
import org.huel.cloudhub.rpc.GrpcChannelPool;
import org.huel.cloudhub.rpc.GrpcProperties;
import org.huel.cloudhub.rpc.GrpcServiceStubPool;
import org.huel.cloudhub.rpc.StreamObserverWrapper;
import org.huel.cloudhub.server.rpc.heartbeat.SerializedFileServer;
import org.huel.cloudhub.util.math.Maths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.TimeUnit;

/**
 * @author RollW
 */
@Service
public class ReplicaService {
    private final GrpcProperties grpcProperties;
    private final SerializedFileServer server;
    private final FileServerChannelPool fileServerChannelPool;
    private final GrpcServiceStubPool<ReplicaServiceGrpc.ReplicaServiceStub>
            replicaServiceStubPool;
    private final ContainerReadOpener containerReadOpener;
    private final ContainerChecker containerChecker;
    private final Logger logger = LoggerFactory.getLogger(ReplicaService.class);

    public ReplicaService(GrpcProperties grpcProperties,
                          SourceServerGetter sourceServerGetter,
                          ContainerReadOpener containerReadOpener,
                          ContainerChecker containerChecker) {
        this.grpcProperties = grpcProperties;
        this.fileServerChannelPool = new FileServerChannelPool(grpcProperties);
        this.server = toSerializedServer(sourceServerGetter.getLocalServer());
        this.containerChecker = containerChecker;
        this.replicaServiceStubPool = new GrpcServiceStubPool<>();
        this.containerReadOpener = containerReadOpener;
    }

    private SerializedFileServer toSerializedServer(SourceServerGetter.ServerInfo serverInfo) {
        return SerializedFileServer.newBuilder()
                .setPort(serverInfo.port())
                .setId(serverInfo.id())
                .setHost(serverInfo.host())
                .build();
    }

    public void requestReplicasSynchro(List<ReplicaSynchroPart> replicaSynchroParts,
                                       SerializedFileServer dest) {
        if (replicaSynchroParts.isEmpty()) {
            return;
        }
        ReplicaServiceGrpc.ReplicaServiceStub stub =
                requireReplicaServiceStub(dest);
        logger.debug("Load stub successful, target server id={}.", dest.getId());
        PartReplicaStreamObserver observer =
                new PartReplicaStreamObserver(replicaSynchroParts);
        StreamObserver<ReplicaRequest> replicaRequestStreamObserver =
                stub.sendReplica(observer);
        observer.setRequestObserver(replicaRequestStreamObserver);
        observer.onNext(null);
    }

    public void requestReplicaFullSync(Container container,
                                       SerializedFileServer dest) {
        if (container == null) {
            return;
        }

        ReplicaServiceGrpc.ReplicaServiceStub stub =
                requireReplicaServiceStub(dest);
        FullSyncStreamObserver syncStreamObserver =
                new FullSyncStreamObserver(container);
        StreamObserver<ReplicaRequest> replicaRequestStreamObserver =
                stub.sendReplica(syncStreamObserver);
        syncStreamObserver.setRequestObserver(replicaRequestStreamObserver);
        syncStreamObserver.onNext(null);
    }

    public void requestReplicaDelete(Container container, String source,
                                     SerializedFileServer server) {
        ReplicaServiceGrpc.ReplicaServiceStub stub =
                requireReplicaServiceStub(server);
        logger.debug("Start request replica delete for {}", server.getId());
        ReplicaDeleteRequest request = ReplicaDeleteRequest.newBuilder()
                .setSerial(container.getSerial())
                .setSource(source)
                .setId(container.getIdentity().id())
                .build();
        stub.deleteReplica(request, ReplicaDeleteStreamObserver.getInstance());
    }

    private static class ReplicaDeleteStreamObserver implements StreamObserver<ReplicaDeleteResponse> {
        private final Logger logger = LoggerFactory.getLogger(ReplicaDeleteStreamObserver.class);

        @Override
        public void onNext(ReplicaDeleteResponse value) {
        }

        @Override
        public void onError(Throwable t) {
            logger.error("Delete error.", t);
        }

        @Override
        public void onCompleted() {
        }

        public static ReplicaDeleteStreamObserver getInstance() {
            return SingletonHolder.INSTANCE;
        }

        private static final class SingletonHolder {
            static final ReplicaDeleteStreamObserver INSTANCE = new ReplicaDeleteStreamObserver();
        }
    }

    private class PartReplicaStreamObserver implements StreamObserver<ReplicaResponse> {
        private final ListIterator<ReplicaSynchroPart> partIterator;
        private StreamObserverWrapper<ReplicaRequest> requestObserver;
        private String contCrc;
        private int blocksInRequest = 20;

        private PartReplicaStreamObserver(List<ReplicaSynchroPart> replicaSynchroParts) {
            partIterator = replicaSynchroParts.listIterator();
        }

        @Override
        public void onNext(ReplicaResponse value) {
            if (value != null) {
                String checkValue = value.getCheckValue();
                if (!checkValue.equals(contCrc)) {
                    ReplicaSynchroPart part = partIterator.previous();
                    sendFullSync(part.getContainer());
                    partIterator.next();
                    return;
                }
                logger.debug("Received check value={}, saved check value={}.", checkValue, contCrc);
            }
            if (requestObserver.isClose()) {
                return;
            }
            if (!partIterator.hasNext()) {
                requestObserver.onCompleted();
                return;
            }
            logger.debug("Get prepared for next request.");
            ReplicaSynchroPart part = partIterator.next();
            sendPart(part);
        }

        private void sendPart(ReplicaSynchroPart part) {
            if (part == null) {
                return;
            }
            logger.debug("Next part: container_id={}",
                    part.getContainer().getIdentity().id());
            ReplicaRequest.CheckMessage checkMessage =
                    buildPartCheckMessage(part);
            requestObserver.onNext(ReplicaRequest.newBuilder()
                    .setCheckMessage(checkMessage)
                    .build()
            );
            Container container = part.getContainer();
            blocksInRequest = calcBlocksInRequest(container);
            try {
                contCrc = containerChecker.calculateChecksum(container);
                ContainerReader containerReader = new ContainerReader(container, containerReadOpener);
                Read read = new Read(containerReader, part);
                readAndSendNext(read, blocksInRequest);
            } catch (IOException | LockException e) {
                requestObserver.onError(e);
                logger.error("Error: container open or read error.", e);
            }
            if (!partIterator.hasNext()) {
                requestObserver.onNext(buildLastRequest());
            }
        }

        private void readAndSendNext(Read read, final int blocksInRequest) throws IOException {
            if (read == null) {
                return;
            }
            ReplicaSynchroPart part = read.part();
            ContainerReader reader = read.containerReader();

            int[] blocks = part.getBlockGroupsInfo().flatToBlocksIndex();
            List<ContainerBlock> containerBlocks = new ArrayList<>();
            int reads = 0;
            for (int block : blocks) {
                if (reads >= blocksInRequest) {
                    reads = 0;
                    sendReplicaRequest(containerBlocks);
                    containerBlocks = new ArrayList<>();
                }

                containerBlocks.add(reader.readBlock(block));
                reads++;
            }
            sendReplicaRequest(containerBlocks);
            reader.close();
        }

        private void sendReplicaRequest(List<ContainerBlock> containerBlocks) {
            if (containerBlocks.isEmpty()) {
                return;
            }
            List<ReplicaData> replicaDataList = serializedFromBlock(containerBlocks);
            ReplicaRequest replicaRequest = buildDataRequest(replicaDataList);
            requestObserver.onNext(replicaRequest);
        }

        private void sendFullSync(Container container) {
            ReplicaRequest.CheckMessage checkMessage =
                    buildFullSyncCheckMessage(container);


        }


        @Override
        public void onError(Throwable t) {
            logger.error("Container replica process error.", t);
        }

        @Override
        public void onCompleted() {
            logger.debug("Container replica process complete.");
        }

        public void setRequestObserver(StreamObserver<ReplicaRequest> requestObserver) {
            this.requestObserver = new StreamObserverWrapper<>(requestObserver);
        }
    }

    private class FullSyncStreamObserver implements StreamObserver<ReplicaResponse> {
        private final Container container;
        private StreamObserverWrapper<ReplicaRequest> requestObserver;

        private FullSyncStreamObserver(Container container) {
            this.container = container;
        }

        @Override
        public void onNext(ReplicaResponse value) {
            if (value == null) {
                sendFirst();
                return;
            }
        }

        private void sendFirst() {

        }

        @Override
        public void onError(Throwable t) {

        }

        @Override
        public void onCompleted() {

        }

        public void setRequestObserver(StreamObserver<ReplicaRequest> requestObserver) {
            this.requestObserver = new StreamObserverWrapper<>(requestObserver);
        }
    }

    private ReplicaRequest buildLastRequest() {
        return ReplicaRequest.newBuilder()
                .setCheckMessage(ReplicaRequest.CheckMessage.newBuilder()
                        .setLastReq(true)
                        .build())
                .build();
    }

    private ReplicaRequest.CheckMessage buildPartCheckMessage(ReplicaSynchroPart replicaSynchroPart) {
        Container container = replicaSynchroPart.getContainer();
        return buildFullSyncCheckMessage(container);
    }

    private ReplicaRequest.CheckMessage buildFullSyncCheckMessage(Container container) {
        ReplicaRequest.CheckMessage.Builder builder = ReplicaRequest.CheckMessage.newBuilder()
                .setId(container.getIdentity().id())
                .setSerial(container.getSerial())
                .setVersion(container.getVersion())
                .setBlockMeta(toSerializedContainerBlockMeta(container))
                .setSource(server);
        return builder.build();
    }

    private SerializedContainerBlockMeta toSerializedContainerBlockMeta(Container container) {
        ContainerIdentity identity = container.getIdentity();
        return SerializedContainerBlockMeta.newBuilder()
                .addAllBlockMetas(container.getSerializedMetaInfos())
                .setUsedBlock(container.getUsedBlocksCount())
                .setBlockCap(identity.blockLimit())
                .setBlockSize(identity.blockSize())
                .setCrc(identity.crc())
                .build();
    }

    private static class FileServerChannelPool extends GrpcChannelPool<SerializedFileServer> {
        private final GrpcProperties grpcProperties;

        private FileServerChannelPool(GrpcProperties grpcProperties) {
            this.grpcProperties = grpcProperties;
        }

        @Override
        @NonNull
        protected ManagedChannel buildChannel(SerializedFileServer key) {
            return ManagedChannelBuilder.forAddress(key.getHost(), key.getPort())
                    .usePlaintext()
                    .keepAliveTime(5, TimeUnit.MINUTES)
                    .keepAliveTimeout(2, TimeUnit.MINUTES)
                    .maxInboundMessageSize((int) grpcProperties.getMaxRequestSizeBytes() * 2)
                    .build();
        }
    }

    private ReplicaServiceGrpc.ReplicaServiceStub requireReplicaServiceStub(SerializedFileServer server) {
        ManagedChannel managedChannel = fileServerChannelPool.getChannel(server);
        ReplicaServiceGrpc.ReplicaServiceStub stub =
                replicaServiceStubPool.getStub(server.getId());
        if (stub != null) {
            return stub;
        }
        stub = ReplicaServiceGrpc.newStub(managedChannel);
        replicaServiceStubPool.registerStub(server.getId(), stub);
        return stub;
    }

    private ReplicaRequest buildDataRequest(List<ReplicaData> replicaDataList) {
        SerializedReplicaDataInfo replicaDataInfo = SerializedReplicaDataInfo.newBuilder()
                .addAllData(replicaDataList)
                .build();
        return ReplicaRequest.newBuilder()
                .setReplicaDataInfo(replicaDataInfo)
                .build();
    }

    private List<ReplicaData> serializedFromBlock(List<ContainerBlock> containerBlocks) {
        if (containerBlocks == null || containerBlocks.isEmpty()) {
            return List.of();
        }
        List<ReplicaData> replicaDataList = new ArrayList<>();
        for (ContainerBlock containerBlock : containerBlocks) {
            byte[] bytes = containerBlock.getData();
            ReplicaData data = ReplicaData.newBuilder()
                    .setData(ByteString.copyFrom(bytes, 0, bytes.length))
                    .build();
            containerBlock.release();
            replicaDataList.add(data);
        }
        return replicaDataList;
    }

    private int calcBlocksInRequest(Container container) {
        long requestSize = grpcProperties.getMaxRequestSizeBytes();
        long blocksSizeBytes =
                container.getIdentity().blockSizeBytes();
        return Maths.ceilDivideReturnsInt(requestSize, blocksSizeBytes);
    }

    private record Read(ContainerReader containerReader,
                        ReplicaSynchroPart part) {
    }
}
