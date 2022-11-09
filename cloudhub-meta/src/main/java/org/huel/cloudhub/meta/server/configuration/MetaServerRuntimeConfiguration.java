package org.huel.cloudhub.meta.server.configuration;

import org.huel.cloudhub.meta.conf.MetaConfigLoader;
import org.huel.cloudhub.meta.server.service.node.HeartbeatServerProperties;
import org.huel.cloudhub.server.GrpcProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * @author RollW
 */
@Configuration
public class MetaServerRuntimeConfiguration {
    private final MetaConfigLoader metaConfigLoader;

    public MetaServerRuntimeConfiguration() throws IOException {
        this.metaConfigLoader = MetaConfigLoader.tryOpenDefault();
    }

    @Bean
    public HeartbeatServerProperties heartbeatServerProperties() {
        return new HeartbeatServerProperties(
                metaConfigLoader.getHeartbeatStandardPeriod(),
                metaConfigLoader.getHeartbeatTimeoutCycle());
    }

    @Bean
    public GrpcProperties grpcProperties() {
        return new GrpcProperties(
                metaConfigLoader.getRpcPort(),
                metaConfigLoader.getRpcMaxInboundSize());
    }

    @Bean
    public FileProperties fileProperties() {
        return new FileProperties(
                metaConfigLoader.getFileTempPath(),
                metaConfigLoader.getUploadBlockSize());
    }
}
