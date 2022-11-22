package org.huel.cloudhub.meta.server.configuration;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.huel.cloudhub.meta.server.service.file.ClientFileUploadDispatchService;
import org.huel.cloudhub.meta.server.service.file.FileStatusService;
import org.huel.cloudhub.meta.server.service.node.HeartbeatService;
import org.huel.cloudhub.meta.server.service.node.NodeChannelPool;
import org.huel.cloudhub.meta.server.service.server.MetaServerStatusService;
import org.huel.cloudhub.meta.server.service.server.NodeServerStatusService;
import org.huel.cloudhub.rpc.GrpcProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * @author RollW
 */
@Configuration
public class GrpcMetaServerConfiguration {
    private final GrpcProperties grpcProperties;
    private final HeartbeatService heartbeatService;
    private final FileStatusService fileStatusService;
    private final ClientFileUploadDispatchService clientFileUploadDispatchService;
    private final MetaServerStatusService metaServerStatusService;
    private final NodeServerStatusService nodeServerStatusService;

    public GrpcMetaServerConfiguration(GrpcProperties grpcProperties,
                                       HeartbeatService heartbeatService,
                                       FileStatusService fileStatusService,
                                       ClientFileUploadDispatchService clientFileUploadDispatchService,
                                       MetaServerStatusService metaServerStatusService,
                                       NodeServerStatusService nodeServerStatusService) {
        this.grpcProperties = grpcProperties;
        this.heartbeatService = heartbeatService;
        this.fileStatusService = fileStatusService;
        this.clientFileUploadDispatchService = clientFileUploadDispatchService;
        this.metaServerStatusService = metaServerStatusService;
        this.nodeServerStatusService = nodeServerStatusService;
    }

    @Bean
    public Server grpcServer() {
        return ServerBuilder.forPort(grpcProperties.getPort())
                .maxInboundMessageSize((int) grpcProperties.getMaxRequestSizeBytes())
                .handshakeTimeout(20, TimeUnit.SECONDS)
                .maxConnectionAge(2, TimeUnit.MINUTES)
                .addService(heartbeatService)
                .addService(fileStatusService)
                .addService(clientFileUploadDispatchService)
                .addService(metaServerStatusService)
                .addService(nodeServerStatusService)
                .build();
    }

    @Bean
    public NodeChannelPool nodeChannelPool() {
        return new NodeChannelPool(grpcProperties);
    }
}
