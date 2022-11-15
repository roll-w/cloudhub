package org.huel.cloudhub.file.server.service.heartbeat;

import io.grpc.ManagedChannel;
import org.huel.cloudhub.file.server.service.SourceServerGetter;
import org.huel.cloudhub.server.rpc.heartbeat.Heartbeat;
import org.huel.cloudhub.server.rpc.heartbeat.HeartbeatResponse;
import org.huel.cloudhub.server.rpc.heartbeat.HeartbeatServiceGrpc;
import org.huel.cloudhub.server.rpc.status.SerializedServerStatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author RollW
 */
@Service
public class ServerHeartbeatService {
    private final HeartbeatServiceGrpc.HeartbeatServiceBlockingStub serviceStub;
    private final Logger logger = LoggerFactory.getLogger(ServerHeartbeatService.class);
    private final SourceServerGetter.ServerInfo serverInfo;

    public ServerHeartbeatService(ManagedChannel channel,
                                  SourceServerGetter sourceServerGetter) {
        this.serviceStub = HeartbeatServiceGrpc.newBlockingStub(channel);
        this.serverInfo = sourceServerGetter.getLocalServer();
    }


    public HeartbeatResponse sendHeartbeat() {
        Heartbeat heartbeat = Heartbeat.newBuilder()
                .setHost(serverInfo.host())
                .setPort(serverInfo.port())
                .setId(serverInfo.id())
                .setStatusCode(SerializedServerStatusCode.HEALTHY)
                .build();
        return serviceStub.receiveHeartbeat(heartbeat);
    }
}
