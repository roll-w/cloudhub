package org.huel.cloudhub.file.server.heartbeat;

import io.grpc.ManagedChannel;
import org.huel.cloudhub.server.rpc.proto.Heartbeat;
import org.huel.cloudhub.server.rpc.proto.HeartbeatResponse;
import org.huel.cloudhub.server.rpc.proto.HeartbeatServiceGrpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.InetAddress;

/**
 * @author RollW
 */
@Service
public class HeartbeatSendService {
    private final ManagedChannel channel;
    private final HeartbeatServiceGrpc.HeartbeatServiceBlockingStub serviceStub;
    private final Logger logger = LoggerFactory.getLogger(HeartbeatSendService.class);
    private final InetAddress inetAddress;

    public HeartbeatSendService(ManagedChannel channel, InetAddress inetAddress) {
        this.channel = channel;
        this.serviceStub = HeartbeatServiceGrpc.newBlockingStub(channel);
        this.inetAddress = inetAddress;
    }

    public void sendHeartbeat() {
        Heartbeat heartbeat = Heartbeat.newBuilder()
                .setHost(inetAddress.getHostAddress())
                .setPort("7021")
                .build();
        logger.info("send heartbeat, address= {}:{}", heartbeat.getHost(), heartbeat.getPort());
        HeartbeatResponse response =
                serviceStub.receiveHeartbeat(heartbeat);
        logger.info("receive response: errorCode: {}", response.getErrorCode());
    }
}
