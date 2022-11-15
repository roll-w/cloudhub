package org.huel.cloudhub.file.server.service.replica;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.huel.cloudhub.file.fs.LockException;
import org.huel.cloudhub.file.fs.block.Block;
import org.huel.cloudhub.file.fs.container.Container;
import org.huel.cloudhub.file.fs.container.ContainerChecker;
import org.huel.cloudhub.file.fs.container.ContainerWriter;
import org.huel.cloudhub.file.fs.container.ContainerWriterOpener;
import org.huel.cloudhub.file.fs.container.replica.ReplicaContainerCreator;
import org.huel.cloudhub.file.fs.container.replica.ReplicaContainerNameMeta;
import org.huel.cloudhub.file.rpc.replica.*;
import org.huel.cloudhub.file.server.service.SourceServerGetter;
import org.huel.cloudhub.rpc.StreamObserverWrapper;
import org.huel.cloudhub.server.rpc.heartbeat.SerializedFileServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author RollW
 */
@Service
public class ReplicaReceiveService extends ReplicaServiceGrpc.ReplicaServiceImplBase {
    private final Logger logger = LoggerFactory.getLogger(ReplicaReceiveService.class);
    private final ReplicaContainerCreator replicaContainerCreator;
    private final ContainerWriterOpener containerWriterOpener;
    private final ContainerChecker containerChecker;
    private final SourceServerGetter.ServerInfo serverInfo;

    public ReplicaReceiveService(ReplicaContainerCreator replicaContainerCreator,
                                 ContainerWriterOpener containerWriterOpener,
                                 ContainerChecker containerChecker,
                                 SourceServerGetter sourceServerGetter) {
        this.replicaContainerCreator = replicaContainerCreator;
        this.containerWriterOpener = containerWriterOpener;
        this.containerChecker = containerChecker;
        this.serverInfo = sourceServerGetter.getLocalServer();
    }

    @Override
    public StreamObserver<ReplicaRequest> sendReplica(StreamObserver<ReplicaResponse> responseObserver) {
        return new ReplicaRequestObserver(responseObserver);
    }

    private boolean checkId(String id) {
        return serverInfo.id().equals(id);
    }

    private class ReplicaRequestObserver implements StreamObserver<ReplicaRequest> {
        private final StreamObserverWrapper<ReplicaResponse> responseObserver;
        private ContainerWriter containerWriter;
        private Container replicaContainer;

        public ReplicaRequestObserver(StreamObserver<ReplicaResponse> responseObserver) {
            this.responseObserver = new StreamObserverWrapper<>(responseObserver);
        }

        private void saveCheckMessage(ReplicaRequest.CheckMessage checkMessage) throws LockException, IOException {
            if (containerWriter != null) {
                containerWriter.close();
            }
            if (checkId(checkMessage.getSource().getId())) {
                logger.debug("id equals");
                responseObserver.onError(Status.INVALID_ARGUMENT.asException());
                return;
            }
            ReplicaContainerNameMeta nameMeta = new ReplicaContainerNameMeta(
                    checkMessage.getSource().getId(),
                    checkMessage.getId(),
                    checkMessage.getSerial());
            replicaContainer = replicaContainerCreator.findOrCreateContainer(nameMeta.getId(),
                    nameMeta.getSourceId(), nameMeta.getSerial(), checkMessage.getBlockMeta());
            replicaContainerCreator.createContainerWithMeta(replicaContainer, checkMessage.getBlockMeta());
            containerWriter = new ContainerWriter(replicaContainer, containerWriterOpener);
        }

        @Override
        public void onNext(ReplicaRequest value) {
            if (value.getReplicaMessageCase() == ReplicaRequest.ReplicaMessageCase.REPLICAMESSAGE_NOT_SET) {
                responseObserver.onError(Status.INVALID_ARGUMENT.asException());
                return;
            }
            if (value.getReplicaMessageCase() == ReplicaRequest.ReplicaMessageCase.CHECK_MESSAGE) {
                sendCheckValue(replicaContainer);// TODO: check value

                ReplicaRequest.CheckMessage checkMessage = value.getCheckMessage();
                SerializedFileServer server = checkMessage.getSource();
                // means a new container replica.
                try {
                    saveCheckMessage(checkMessage);
                } catch (LockException | IOException e) {
                    logger.error("Error occurred while initialize message.", e);
                }

                logger.debug("Received message, source server={};address={}:{}",
                        server.getId(), server.getHost(), server.getPort());
                return;
            }
            trySeek(value.getReplicaDataInfo(), containerWriter);
            List<Block> blocks = toBlockList(value.getReplicaDataInfo().getDataList());
            try {
                containerWriter.write(blocks, true);
            } catch (IOException e) {
                // FIXME: ReachLimit Exception, It should be caused by sending and reading more than expect
                logger.error("Error: write error, message=%s".formatted(e.getMessage()), e);
            }
            logger.debug("write replica data");
        }


        @Override
        public void onError(Throwable t) {
            logger.debug("Error occurred while receive replicas.", t);
        }

        @Override
        public void onCompleted() {
            if (containerWriter != null) {
                try {
                    containerWriter.close();
                } catch (IOException e) {
                    logger.debug("Error occurred while closing the container.", e);
                }
            }
            sendCheckValue(replicaContainer);
            responseObserver.onCompleted();
        }

        private void sendCheckValue(Container container) {
            if (container == null) {
                return;
            }
            logger.debug("send check value: cont={}.", container.getResourceLocator());
            try {
                String value = containerChecker.calculateChecksum(container);
                responseObserver.onNext(ReplicaResponse.newBuilder()
                        .setCheckValue(value).build());
            } catch (LockException | IOException e) {
                logger.error("Container crc32 calc failed.", e);
                responseObserver.onError(e);
            }
        }
    }

    private List<Block> toBlockList(List<ReplicaData> replicaDataList) {
        List<Block> blocks = new ArrayList<>();
        for (ReplicaData replicaData : replicaDataList) {
            byte[] data = replicaData.toByteArray();
            Block block = new Block(data, data.length);
            blocks.add(block);
        }
        return blocks;
    }

    private void trySeek(SerializedReplicaDataInfo dataInfo,
                         ContainerWriter writer) {
        if (!dataInfo.hasBlockInfo()) {
            return;
        }
        int i = dataInfo.getBlockInfo().getStartIndex();
        if (i < 0) {
            return;
        }
        try {
            writer.seek(i);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
