package org.huel.cloudhub.meta.server.configuration;

import org.huel.cloudhub.meta.conf.MetaConfigLoader;
import org.huel.cloudhub.meta.server.service.node.HeartbeatServerProperties;
import org.huel.cloudhub.rpc.GrpcProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import space.lingu.light.DatasourceConfig;

import java.io.File;
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
        File file = new File(metaConfigLoader.getFileDataPath());
        if (!file.exists()) {
            file.mkdirs();
        }

        return new FileProperties(
                metaConfigLoader.getFileDataPath(),
                metaConfigLoader.getFileTempPath(),
                metaConfigLoader.getUploadBlockSize());
    }

    @Bean
    public DatasourceConfig datasourceConfig() {
        final String dbPath = fileProperties().getDataPath() + "/meta.db";
        return new DatasourceConfig(
                "jdbc:sqlite:" + dbPath,
                "org.sqlite.JDBC",
                null, null);
    }
}
