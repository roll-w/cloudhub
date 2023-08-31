/*
 * Cloudhub - A high available, scalable distributed file system.
 * Copyright (C) 2022 Cloudhub
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.cloudhub.file.server.service.replica;

import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.stub.StreamObserver;
import org.cloudhub.file.fs.LockException;
import org.cloudhub.file.fs.block.ContainerBlock;
import org.cloudhub.file.fs.container.*;
import org.cloudhub.file.fs.meta.SerializedContainerBlockMeta;
import org.cloudhub.file.rpc.replica.*;
import org.cloudhub.file.server.service.ClientFileServerChannelPool;
import org.cloudhub.server.ServerInfo;
import org.cloudhub.server.SourceServerGetter;
import org.cloudhub.rpc.GrpcProperties;
import org.cloudhub.rpc.GrpcServiceStubPool;
import org.cloudhub.rpc.StreamObserverWrapper;
import org.cloudhub.server.rpc.server.SerializedFileServer;
import org.cloudhub.util.math.Maths;
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
    private final ClientFileServerChannelPool clientFileServerChannelPool;
    private final GrpcServiceStubPool<ReplicaServiceGrpc.ReplicaServiceStub>
            replicaServiceStubPool;
    private final ContainerReadOpener containerReadOpener;
    private final ContainerChecker containerChecker;
    private final Logger logger = LoggerFactory.getLogger(ReplicaService.class);

    public ReplicaService(GrpcProperties grpcProperties,
                          ClientFileServerChannelPool clientFileServerChannelPool,
                          SourceServerGetter sourceServerGetter,
                          ContainerReadOpener containerReadOpener,
                          ContainerChecker containerChecker) {
        this.grpcProperties = grpcProperties;
        this.server = toSerializedServer(sourceServerGetter.getLocalServer());
        this.clientFileServerChannelPool = clientFileServerChannelPool;
        this.containerChecker = containerChecker;
        this.replicaServiceStubPool = new GrpcServiceStubPool<>();
        this.containerReadOpener = containerReadOpener;
    }

    private SerializedFileServer toSerializedServer(ServerInfo serverInfo) {
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
            requestObserver.onNext(ReplicaRequest.newBuilder()
                    .setCheckMessage(checkMessage)
                    .build()
            );
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

    private ReplicaServiceGrpc.ReplicaServiceStub requireReplicaServiceStub(SerializedFileServer server) {
        ManagedChannel managedChannel = clientFileServerChannelPool.getChannel(server);
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
