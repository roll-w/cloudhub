/*
 * Cloudhub - A high available, scalable distributed file system.
 * Copyright (C) 2022 Cloudhub
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.cloudhub.meta.server.configuration;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.cloudhub.meta.server.service.file.ClientFileDeleteDispatchService;
import org.cloudhub.meta.server.service.file.ClientFileUploadDispatchService;
import org.cloudhub.meta.server.service.file.FileStatusService;
import org.cloudhub.meta.server.service.node.HeartbeatService;
import org.cloudhub.meta.server.service.server.MetaServerStatusService;
import org.cloudhub.meta.server.service.server.NodeServerStatusService;
import org.cloudhub.rpc.GrpcProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
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
    private final ClientFileDeleteDispatchService clientFileDeleteDispatchService;

    public GrpcMetaServerConfiguration(GrpcProperties grpcProperties,
                                       HeartbeatService heartbeatService,
                                       FileStatusService fileStatusService,
                                       ClientFileUploadDispatchService clientFileUploadDispatchService,
                                       MetaServerStatusService metaServerStatusService,
                                       NodeServerStatusService nodeServerStatusService,
                                       ClientFileDeleteDispatchService clientFileDeleteDispatchService) {
        this.grpcProperties = grpcProperties;
        this.heartbeatService = heartbeatService;
        this.fileStatusService = fileStatusService;
        this.clientFileUploadDispatchService = clientFileUploadDispatchService;
        this.metaServerStatusService = metaServerStatusService;
        this.nodeServerStatusService = nodeServerStatusService;
        this.clientFileDeleteDispatchService = clientFileDeleteDispatchService;
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
                .addService(clientFileDeleteDispatchService)
                .executor(threadPoolExecutor)
                .build();
    }

    private static final int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
    private static final int KEEP_ALIVE_TIME = 10;
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.MINUTES;
    private final BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>();
    private final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
            NUMBER_OF_CORES,
            NUMBER_OF_CORES * 20,
            KEEP_ALIVE_TIME,
            KEEP_ALIVE_TIME_UNIT,
            workQueue,
            new BasicThreadFactory.Builder()
                    .namingPattern("ault-executor-%d")
                    .priority(Thread.MAX_PRIORITY)
                    .build()
    );
}
