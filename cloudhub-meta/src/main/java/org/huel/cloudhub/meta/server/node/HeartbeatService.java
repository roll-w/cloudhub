package org.huel.cloudhub.meta.server.node;

import io.grpc.stub.StreamObserver;
import org.huel.cloudhub.server.rpc.proto.Heartbeat;
import org.huel.cloudhub.server.rpc.proto.HeartbeatResponse;
import org.huel.cloudhub.server.rpc.proto.HeartbeatServiceGrpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author RollW
 */
@Service
public class HeartbeatService extends HeartbeatServiceGrpc.HeartbeatServiceImplBase {
    private final Logger logger = LoggerFactory.getLogger(HeartbeatService.class);

    public HeartbeatService() {
    }

    @Override
    public void receiveHeartbeat(Heartbeat request, StreamObserver<HeartbeatResponse> responseObserver) {
        logger.info("receive heartbeat, address: {}:{}, id: {}",
                request.getHost(), request.getPort(), request.getId());
        responseObserver.onNext(
                HeartbeatResponse.newBuilder()
                        .setErrorCode("00000")
                        .setMessage("success")
                        .build()
        );
        responseObserver.onCompleted();
    }
}
