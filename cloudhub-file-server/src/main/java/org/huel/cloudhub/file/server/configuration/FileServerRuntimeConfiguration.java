package org.huel.cloudhub.file.server.configuration;

import org.huel.cloudhub.conf.ConfigurationException;
import org.huel.cloudhub.file.conf.FileConfigKeys;
import org.huel.cloudhub.file.conf.FileConfigLoader;
import org.huel.cloudhub.file.fs.container.ContainerProperties;
import org.huel.cloudhub.file.server.service.heartbeat.HeartbeatHostProperties;
import org.huel.cloudhub.rpc.GrpcProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * @author RollW
 */
@Configuration
public class FileServerRuntimeConfiguration {
    private final FileConfigLoader fileConfigLoader;

    public FileServerRuntimeConfiguration() throws IOException {
        this.fileConfigLoader = FileConfigLoader.tryOpenDefault();
    }

    @Bean
    public GrpcProperties grpcProperties() {
        return new GrpcProperties(
                fileConfigLoader.getRpcPort(),
                fileConfigLoader.getRpcMaxInboundSize());
    }

    @Bean
    public ContainerProperties containerProperties() {
        return new ContainerProperties(
                fileConfigLoader.getFileStorePath(),
                fileConfigLoader.getStagingFilePath(),
                fileConfigLoader.getBlockSize(),
                fileConfigLoader.getBlockNum());
    }

    @Bean
    public HeartbeatHostProperties heartbeatHostProperties() {
        final String address = fileConfigLoader.getMetaServerAddress();
        if (address == null) {
            throw new ConfigurationException("Required value not set on config key=" +
                    FileConfigKeys.META_ADDRESS);
        }

        return new HeartbeatHostProperties(fileConfigLoader.getMetaServerAddress(), 200);
    }
}
