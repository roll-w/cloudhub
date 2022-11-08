package org.huel.cloudhub.file.server.configuration;

import org.huel.cloudhub.file.conf.ConfigKeys;
import org.huel.cloudhub.file.conf.ConfigLoader;
import org.huel.cloudhub.file.conf.ConfigurationException;
import org.huel.cloudhub.file.fs.container.ContainerProperties;
import org.huel.cloudhub.file.server.service.GrpcProperties;
import org.huel.cloudhub.file.server.service.heartbeat.HeartbeatHostProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * @author RollW
 */
@Configuration
public class ServerRuntimeConfiguration {
    @Bean
    public GrpcProperties grpcProperties() throws IOException {
        ConfigLoader configLoader = configLoader();

        return new GrpcProperties(
                configLoader.getRpcPort(),
                configLoader.getRpcMaxInboundSize());
    }

    @Bean
    public ContainerProperties containerProperties() throws IOException {
        ConfigLoader configLoader = configLoader();
        return new ContainerProperties(
                configLoader.getFileStorePath(),
                configLoader.getStagingFilePath(),
                configLoader.getBlockSize(),
                configLoader.getBlockNum());
    }

    @Bean
    public HeartbeatHostProperties heartbeatHostProperties() throws IOException {
        ConfigLoader configLoader = configLoader();
        final String address = configLoader.getMetaServerAddress();
        if (address == null) {
            throw new ConfigurationException("Required value not set on config key=" +
                    ConfigKeys.META_ADDRESS);
        }

        return new HeartbeatHostProperties(configLoader.getMetaServerAddress(), 200);
    }

    @Bean
    public ConfigLoader configLoader() throws IOException {
        return ConfigLoader.tryOpenDefault();
    }
}
