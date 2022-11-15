package org.huel.cloudhub.file.server.service.replica;

import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.huel.cloudhub.file.fs.LockException;
import org.huel.cloudhub.file.fs.block.ContainerBlock;
import org.huel.cloudhub.file.fs.container.Container;
import org.huel.cloudhub.file.fs.container.ContainerChecker;
import org.huel.cloudhub.file.fs.container.ContainerReadOpener;
import org.huel.cloudhub.file.fs.container.ContainerReader;
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
        ReplicaServiceStreamObserver observer =
                new ReplicaServiceStreamObserver(replicaSynchroParts);
        StreamObserver<ReplicaRequest> replicaRequestStreamObserver =
                stub.sendReplica(observer);
        observer.setRequestObserver(replicaRequestStreamObserver);
        observer.onNext(null);
    }

    private class ReplicaServiceStreamObserver implements StreamObserver<ReplicaResponse> {
        private final ListIterator<ReplicaSynchroPart> partIterator;
        private StreamObserverWrapper<ReplicaRequest> requestObserver;
        private String contCrc;
        private Read lastRead = null;
        private int blocksInRequest = 20;

        private ReplicaServiceStreamObserver(List<ReplicaSynchroPart> replicaSynchroParts) {
            partIterator = replicaSynchroParts.listIterator();
        }

        @Override
        public void onNext(ReplicaResponse value) {
            if (value != null) {
                String checkValue = value.getCheckValue();
                logger.info("Received check value={}, saved check value={}.", checkValue, contCrc);
            }
            logger.debug("Get prepared for next request.");
            if (!partIterator.hasNext()) {
                requestObserver.onCompleted();
                return;
            }
            if (lastRead != null) {
                logger.debug("Last read not null, probably be a bug.");
                return;
            }
            ReplicaSynchroPart part = partIterator.next();
            logger.debug("Next part: `container_id`={}",
                    part.getContainer().getIdentity().id());
            ReplicaRequest.CheckMessage checkMessage =
                    buildCheckMessage(part);
            logger.debug("Build check message and send.");
            requestObserver.onNext(ReplicaRequest.newBuilder()
                    .setCheckMessage(checkMessage)
                    .build()
            );
            logger.debug("Send check message successful.");
            Container container = part.getContainer();
            blocksInRequest = calcBlocksInRequest(container);
            try {
                ContainerReader containerReader = new ContainerReader(container, containerReadOpener);
                lastRead = new Read(containerReader, part);
            } catch (IOException | LockException e) {
                requestObserver.onError(e);
                logger.error("Error: container open error.", e);
            }

            try {
                readAndSendNext(lastRead, blocksInRequest);
            } catch (IOException e) {
                logger.error("Error: container read error.", e);
                requestObserver.onError(e);
            }
            lastRead = null;
        }

        private void readAndSendNext(@NonNull Read read, final int blocksInRequest) throws IOException {
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
            logger.debug("Send replica data request.");
            List<ReplicaData> replicaDataList = serializedFromBlock(containerBlocks);
            ReplicaRequest replicaRequest = buildDataRequest(replicaDataList);
            requestObserver.onNext(replicaRequest);
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

    private ReplicaRequest.CheckMessage buildCheckMessage(ReplicaSynchroPart replicaSynchroPart) {
        Container container = replicaSynchroPart.getContainer();

        ReplicaRequest.CheckMessage.Builder builder = ReplicaRequest.CheckMessage.newBuilder()
                .setId(container.getIdentity().id())
                .setSerial(container.getSerial())
                .setVersion(container.getVersion())
                .setSource(server);
        return builder.build();
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
            ReplicaData data = ReplicaData.newBuilder()
                    .setData(ByteString.copyFrom(containerBlock.getData()))
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
