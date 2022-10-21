package org.huel.cloudhub.file.server.heartbeat;

import io.grpc.ManagedChannel;
import org.huel.cloudhub.server.rpc.proto.Heartbeat;
import org.huel.cloudhub.server.rpc.proto.HeartbeatResponse;
import org.huel.cloudhub.server.rpc.proto.HeartbeatServiceGrpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author RollW
 */
@Service
public class HeartbeatSendService {
    private final ManagedChannel channel;
    private final HeartbeatServiceGrpc.HeartbeatServiceBlockingStub serviceStub;
    private final Logger logger = LoggerFactory.getLogger(HeartbeatSendService.class);

    public HeartbeatSendService(ManagedChannel channel,  InetAddress ip) {
        this.channel = channel;
        this.serviceStub = HeartbeatServiceGrpc.newBlockingStub(channel);
    }

    public void sendHeartbeat() {
        InetAddress ip;
        try {
            ip = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }

        Heartbeat heartbeat = Heartbeat.newBuilder()
                .setHost(ip.getHostAddress())
                .setPort("7021")
                .build();
        logger.info("send heartbeat, address= {}:{}", heartbeat.getHost(), heartbeat.getPort());
        HeartbeatResponse response =
                serviceStub.receiveHeartbeat(heartbeat);
        logger.info("receive response: errorCode: {}", response.getErrorCode());
    }
}
