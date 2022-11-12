package org.huel.cloudhub.file.server.service.replica;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.huel.cloudhub.file.rpc.replica.ReplicaRequest;
import org.huel.cloudhub.file.rpc.replica.ReplicaResponse;
import org.huel.cloudhub.file.rpc.replica.ReplicaServiceGrpc;
import org.huel.cloudhub.server.StreamObserverWrapper;
import org.huel.cloudhub.server.rpc.heartbeat.SerializedFileServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author RollW
 */
public class ReplicaReceiveService extends ReplicaServiceGrpc.ReplicaServiceImplBase {
    private final Logger logger = LoggerFactory.getLogger(ReplicaReceiveService.class);

    @Override
    public StreamObserver<ReplicaRequest> sendReplica(StreamObserver<ReplicaResponse> responseObserver) {
        return new ReplicaRequestObserver(responseObserver);
    }

    private class ReplicaRequestObserver implements StreamObserver<ReplicaRequest> {
        private StreamObserverWrapper<ReplicaResponse> responseObserver;

        public ReplicaRequestObserver(StreamObserver<ReplicaResponse> responseObserver) {
            this.responseObserver = new StreamObserverWrapper<>(responseObserver);
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

                logger.debug("received message, source server: {}, address: {}:{}",
                        server.getId(), server.getHost(), server.getPort());
                return;
            }

        }

        @Override
        public void onError(Throwable t) {
        }

        @Override
        public void onCompleted() {

        }
    }
}
