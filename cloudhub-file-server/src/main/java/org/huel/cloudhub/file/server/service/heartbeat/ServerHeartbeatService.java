package org.huel.cloudhub.file.server.service.heartbeat;

import io.grpc.ManagedChannel;
import org.huel.cloudhub.file.server.service.SourceServerGetter;
import org.huel.cloudhub.file.server.service.id.ServerIdService;
import org.huel.cloudhub.rpc.GrpcProperties;
import org.huel.cloudhub.rpc.heartbeat.Heartbeat;
import org.huel.cloudhub.rpc.heartbeat.HeartbeatResponse;
import org.huel.cloudhub.rpc.heartbeat.HeartbeatServiceGrpc;
import org.huel.cloudhub.rpc.status.SerializedServerStatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.InetAddress;

/**
 * @author RollW
 */
@Service
public class ServerHeartbeatService implements SourceServerGetter {
    private final HeartbeatServiceGrpc.HeartbeatServiceBlockingStub serviceStub;
    private final Logger logger = LoggerFactory.getLogger(ServerHeartbeatService.class);
    private final Server server;

    public ServerHeartbeatService(ManagedChannel channel,
                                  InetAddress inetAddress,
                                  GrpcProperties grpcProperties,
                                  ServerIdService serverIdService) {
        this.serviceStub = HeartbeatServiceGrpc.newBlockingStub(channel);
        this.server = new Server(serverIdService.getServerId(),
                inetAddress.getHostAddress(),
                grpcProperties.getPort());
    }

    @Override
    public Server getLocalServer() {
        return server;
    }

    public HeartbeatResponse sendHeartbeat() {
        Heartbeat heartbeat = Heartbeat.newBuilder()
                .setHost(server.host())
                .setPort(server.port())
                .setId(server.id())
                .setStatusCode(SerializedServerStatusCode.HEALTHY)
                .build();
        return serviceStub.receiveHeartbeat(heartbeat);
    }
}
