package org.huel.cloudhub.file.server.service.replica;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.huel.cloudhub.file.fs.LockException;
import org.huel.cloudhub.file.fs.block.Block;
import org.huel.cloudhub.file.fs.container.Container;
import org.huel.cloudhub.file.fs.container.ContainerWriter;
import org.huel.cloudhub.file.fs.container.ContainerWriterOpener;
import org.huel.cloudhub.file.fs.container.replica.ReplicaContainerCreator;
import org.huel.cloudhub.file.fs.container.replica.ReplicaContainerNameMeta;
import org.huel.cloudhub.file.rpc.replica.ReplicaData;
import org.huel.cloudhub.file.rpc.replica.ReplicaRequest;
import org.huel.cloudhub.file.rpc.replica.ReplicaResponse;
import org.huel.cloudhub.file.rpc.replica.ReplicaServiceGrpc;
import org.huel.cloudhub.file.server.service.id.ServerIdService;
import org.huel.cloudhub.rpc.StreamObserverWrapper;
import org.huel.cloudhub.rpc.heartbeat.SerializedFileServer;
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
    private final ServerIdService serverIdService;

    public ReplicaReceiveService(ReplicaContainerCreator replicaContainerCreator,
                                 ContainerWriterOpener containerWriterOpener,
                                 ServerIdService serverIdService) {
        this.replicaContainerCreator = replicaContainerCreator;
        this.containerWriterOpener = containerWriterOpener;
        this.serverIdService = serverIdService;
    }

    @Override
    public StreamObserver<ReplicaRequest> sendReplica(StreamObserver<ReplicaResponse> responseObserver) {
        return new ReplicaRequestObserver(responseObserver);
    }

    private boolean checkId(String id) {
        return serverIdService.getServerId().equals(id);
    }

    private class ReplicaRequestObserver implements StreamObserver<ReplicaRequest> {
        private final StreamObserverWrapper<ReplicaResponse> responseObserver;
        private ContainerWriter containerWriter;

        public ReplicaRequestObserver(StreamObserver<ReplicaResponse> responseObserver) {
            this.responseObserver = new StreamObserverWrapper<>(responseObserver);
        }

        private void saveCheckMessage(ReplicaRequest.CheckMessage checkMessage) throws LockException, IOException {
            if (containerWriter != null) {
                containerWriter.close();
            }
            if (checkId(checkMessage.getSource().getId())) {
                responseObserver.onError(Status.INVALID_ARGUMENT.asException());
                return;
            }

            ReplicaContainerNameMeta nameMeta = new ReplicaContainerNameMeta(
                    checkMessage.getSource().getId(),
                    checkMessage.getId(),
                    checkMessage.getSerial());
            Container replicaContainer = replicaContainerCreator.findOrCreateContainer(nameMeta.getId(),
                    nameMeta.getSourceId(), nameMeta.getSerial(), checkMessage.getBlockMeta());
            replicaContainerCreator.createContainerWithMeta(replicaContainer, checkMessage.getBlockMeta());

            containerWriter = new ContainerWriter(replicaContainer, containerWriterOpener);
            int start = checkMessage.getBlockInfo().getStartIndex();
            if (!checkMessage.hasBlockInfo() || start <= 0) {
                return;
            }
            containerWriter.seek(start);
        }

        @Override
        public void onNext(ReplicaRequest value) {
            if (value.getReplicaMessageCase() == ReplicaRequest.ReplicaMessageCase.REPLICAMESSAGE_NOT_SET) {
                responseObserver.onError(Status.INVALID_ARGUMENT.asException());
                return;
            }
            if (value.getReplicaMessageCase() == ReplicaRequest.ReplicaMessageCase.CHECK_MESSAGE) {
                ReplicaRequest.CheckMessage checkMessage = value.getCheckMessage();
                SerializedFileServer server = checkMessage.getSource();
                // means a new container replica.
                try {
                    saveCheckMessage(checkMessage);
                } catch (LockException | IOException e) {
                    logger.error("Error occurred while initialize message.", e);
                }
                sendCheckValue("0");// TODO: check value
                logger.debug("Received message, source server={};address={}:{}",
                        server.getId(), server.getHost(), server.getPort());
                return;
            }
            List<Block> blocks = toBlockList(value.getReplicaDataInfo().getDataList());

            try {
                containerWriter.write(blocks, true);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
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
            sendCheckValue(":");
            responseObserver.onCompleted();
        }

        private void sendCheckValue(String value) {
            responseObserver.onNext(ReplicaResponse.newBuilder()
                    .setCheckValue(value).build());
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
}
