package org.huel.cloudhub.file.server.heartbeat;

import io.grpc.ManagedChannel;
import org.huel.cloudhub.file.server.id.ServerIdService;
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
public class ServerHeartbeatService {
    private final ManagedChannel channel;
    private final HeartbeatServiceGrpc.HeartbeatServiceBlockingStub serviceStub;
    private final Logger logger = LoggerFactory.getLogger(ServerHeartbeatService.class);
    private final InetAddress inetAddress;
    private final ServerIdService serverIdService;

    public ServerHeartbeatService(ManagedChannel channel,
                                  InetAddress inetAddress,
                                  ServerIdService serverIdService) {
        this.channel = channel;
        this.serviceStub = HeartbeatServiceGrpc.newBlockingStub(channel);
        this.inetAddress = inetAddress;
        this.serverIdService = serverIdService;
    }

    public void sendHeartbeat() {
        Heartbeat heartbeat = Heartbeat.newBuilder()
                .setHost(inetAddress.getHostAddress())
                .setPort(7021)
                .setId(serverIdService.getServerId())
                .build();
        logger.info("send heartbeat, address= {}:{}", heartbeat.getHost(), heartbeat.getPort());
        HeartbeatResponse response =
                serviceStub.receiveHeartbeat(heartbeat);
        logger.info("receive response: errorCode={};message={};period={}",
                response.getErrorCode(), response.getMessage(), response.getPeriod());
    }
}
