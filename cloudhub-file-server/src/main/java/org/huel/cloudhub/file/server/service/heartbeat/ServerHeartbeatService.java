package org.huel.cloudhub.file.server.service.heartbeat;

import io.grpc.ManagedChannel;
import org.huel.cloudhub.file.server.service.GrpcProperties;
import org.huel.cloudhub.file.server.service.id.ServerIdService;
import org.huel.cloudhub.server.rpc.heartbeat.Heartbeat;
import org.huel.cloudhub.server.rpc.heartbeat.HeartbeatResponse;
import org.huel.cloudhub.server.rpc.heartbeat.HeartbeatServiceGrpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.InetAddress;

/**
 * @author RollW
 */
@Service
public class ServerHeartbeatService {
    private final HeartbeatServiceGrpc.HeartbeatServiceBlockingStub serviceStub;
    private final Logger logger = LoggerFactory.getLogger(ServerHeartbeatService.class);
    private final InetAddress inetAddress;
    private final GrpcProperties grpcProperties;
    private final ServerIdService serverIdService;

    public ServerHeartbeatService(ManagedChannel channel,
                                  InetAddress inetAddress,
                                  GrpcProperties grpcProperties,
                                  ServerIdService serverIdService) {
        this.serviceStub = HeartbeatServiceGrpc.newBlockingStub(channel);
        this.inetAddress = inetAddress;
        this.grpcProperties = grpcProperties;
        this.serverIdService = serverIdService;
    }

    public HeartbeatResponse sendHeartbeat() {
        Heartbeat heartbeat = Heartbeat.newBuilder()
                .setHost(inetAddress.getHostAddress())
                .setPort(grpcProperties.getPort())
                .setId(serverIdService.getServerId())
                .build();
        // logger.info("send heartbeat, address= {}:{}", heartbeat.getHost(), heartbeat.getPort());
        HeartbeatResponse response =
                serviceStub.receiveHeartbeat(heartbeat);
        // logger.info("receive response: errorCode={};message={};period={}",
        //        response.getErrorCode(), response.getMessage(), response.getPeriod());
        return response;
    }

}
