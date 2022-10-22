package org.huel.cloudhub.meta.server.node;

import io.grpc.stub.StreamObserver;
import org.huel.cloudhub.server.rpc.proto.Heartbeat;
import org.huel.cloudhub.server.rpc.proto.HeartbeatResponse;
import org.huel.cloudhub.server.rpc.proto.HeartbeatServiceGrpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author RollW
 */
@Service
public class HeartbeatService extends HeartbeatServiceGrpc.HeartbeatServiceImplBase {
    private final Logger logger = LoggerFactory.getLogger(HeartbeatService.class);

    private final HeartbeatServerProperties heartbeatServerProperties;
    private final HeartbeatWatcherPool heartbeatWatcherPool;
    private final RegisterNodePool registerNodePool;
    private final int timeoutTime;

    public HeartbeatService(HeartbeatServerProperties heartbeatServerProperties) {
        this.heartbeatServerProperties = heartbeatServerProperties;
        this.timeoutTime = heartbeatServerProperties.getTimeoutCycle() * heartbeatServerProperties.getStandardPeriod();

        this.registerNodePool = new RegisterNodePool();
        this.heartbeatWatcherPool = new HeartbeatWatcherPool(
                heartbeatServerProperties.getStandardPeriod(),
                heartbeatServerProperties.getTimeoutCycle(),
                registerNodePool
        );
        heartbeatWatcherPool.start();
        // or HeartbeatConfiguration?
    }

    @Override
    public void receiveHeartbeat(Heartbeat request, StreamObserver<HeartbeatResponse> responseObserver) {
//        logger.info("receive heartbeat, address: {}:{}, id: {}",
//                request.getHost(), request.getPort(), request.getId());
        if (!registerNodePool.isActive(request.getId())) {
            responseObserver.onNext(
                    HeartbeatResponse.newBuilder()
                            .setErrorCode("00000")
                            .setMessage("first time registration.")
                            .setPeriod(heartbeatServerProperties.getStandardPeriod())
                            .build()
            );
            NodeServer nodeServer = NodeServer.fromHeartbeat(request);
            registerNodePool.registerNodeServer(nodeServer);
            heartbeatWatcherPool.pushNodeServerWatcher(nodeServer);

            responseObserver.onCompleted();
            return;
        }
        heartbeatWatcherPool.updateWatcher(request);
        responseObserver.onNext(
                HeartbeatResponse.newBuilder()
                        .setErrorCode("00000")
                        .setMessage("update success.")
                        .build()
        );
        responseObserver.onCompleted();
    }

    public List<HeartbeatWatcher> activeHeartbeatWatchers() {
        return heartbeatWatcherPool.activeWatchers();
    }

    public List<NodeServer> activeServers() {
        return registerNodePool.getActiveNodes();
    }

}
