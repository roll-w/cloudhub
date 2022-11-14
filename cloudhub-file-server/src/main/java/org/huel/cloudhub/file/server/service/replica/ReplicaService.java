package org.huel.cloudhub.file.server.service.replica;

import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.huel.cloudhub.file.fs.LockException;
import org.huel.cloudhub.file.fs.block.ContainerBlock;
import org.huel.cloudhub.file.fs.container.Container;
import org.huel.cloudhub.file.fs.container.ContainerReadOpener;
import org.huel.cloudhub.file.fs.container.ContainerReader;
import org.huel.cloudhub.file.rpc.replica.*;
import org.huel.cloudhub.file.server.service.SourceServerGetter;
import org.huel.cloudhub.rpc.GrpcChannelPool;
import org.huel.cloudhub.rpc.GrpcProperties;
import org.huel.cloudhub.rpc.GrpcServiceStubPool;
import org.huel.cloudhub.rpc.StreamObserverWrapper;
import org.huel.cloudhub.rpc.heartbeat.SerializedFileServer;
import org.huel.cloudhub.util.math.Maths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

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
    private final Logger logger = LoggerFactory.getLogger(ReplicaService.class);

    public ReplicaService(GrpcProperties grpcProperties,
                          SourceServerGetter sourceServerGetter,
                          ContainerReadOpener containerReadOpener) {
        this.grpcProperties = grpcProperties;
        this.fileServerChannelPool = new FileServerChannelPool(grpcProperties);
        this.server = toSerializedServer(sourceServerGetter.getLocalServer());
        this.replicaServiceStubPool = new GrpcServiceStubPool<>();
        this.containerReadOpener = containerReadOpener;
    }

    private SerializedFileServer toSerializedServer(SourceServerGetter.Server server) {
        return SerializedFileServer.newBuilder()
                .setPort(server.port())
                .setId(server.id())
                .setHost(server.host())
                .build();
    }

    public void requestReplicasSynchro(List<ReplicaSynchroPart> replicaSynchroParts,
                                       SerializedFileServer dest) {
        if (replicaSynchroParts.isEmpty()) {
            return;
        }
        ReplicaServiceGrpc.ReplicaServiceStub stub =
                requireReplicaServiceStub(dest);

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
        private Read lastRead = null;
        private int blocksInRequest = 20;

        private ReplicaServiceStreamObserver(List<ReplicaSynchroPart> replicaSynchroParts) {
            partIterator = replicaSynchroParts.listIterator();
        }

        @Override
        public void onNext(ReplicaResponse value) {
            if (value != null) {
                String checkValue = value.getCheckValue();
            }

            // TODO: check value is equals or not.
            if (!partIterator.hasNext()) {
                requestObserver.onCompleted();
                return;
            }
            if (lastRead != null) {
                try {
                    sendUtilEnd(lastRead, blocksInRequest);
                } catch (IOException e) {
                    requestObserver.onError(e);
                    logger.error("Error: container read error.", e);
                }
                lastRead = null;
                return;
            }

            ReplicaSynchroPart part = partIterator.next();
            ReplicaRequest.CheckMessage checkMessage =
                    buildCheckMessage(part);
            requestObserver.onNext(ReplicaRequest.newBuilder()
                    .setCheckMessage(checkMessage)
                    .build()
            );
            Container container = part.getContainer();
            blocksInRequest = calcBlocksInRequest(container);
            try {
                ContainerReader containerReader = new ContainerReader(container, containerReadOpener);
                lastRead = new Read(containerReader, part, 0);
            } catch (IOException | LockException e) {
                requestObserver.onError(e);
                logger.error("Error: container open error.", e);
            }
        }

        private void sendUtilEnd(@NonNull Read read, final int blocksInRequest) throws IOException {
            Read tRead = read;
            while (tRead != null) {
                tRead = readAndSendNext(tRead, blocksInRequest);
            }
        }

        private Read readAndSendNext(@NonNull Read read, final int blocksInRequest) throws IOException {
            ReplicaSynchroPart part = read.part();
            if (read.readBlocks() >= part.getCount()) {
                read.containerReader().close();
                return null;
            }
            ContainerReader reader = read.containerReader();
            final int readBlocks = read.readBlocks, count = part.getCount();
            if (readBlocks == 0) {
                reader.seek(part.getStart());
            }
            int thisRead = blocksInRequest;
            if (readBlocks + blocksInRequest >= count) {
                thisRead = count - readBlocks;
            }
            List<ContainerBlock> reads = reader.readBlocks(thisRead);
            List<ReplicaData> replicaDataList = serializedFromBlock(reads);
            ReplicaRequest replicaRequest = buildDataRequest(replicaDataList);
            requestObserver.onNext(replicaRequest);

            return new Read(read.containerReader, part, readBlocks + thisRead);
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
        if (replicaSynchroPart.getStart() > 0) {
            builder.setBlockInfo(ReplicaBlockInfo.newBuilder()
                    .setStartIndex(replicaSynchroPart.getStart())
                    .setEndIndex(replicaSynchroPart.getEnd())
                    .build()
            );
        }
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
                        ReplicaSynchroPart part, int readBlocks) {
    }
}
