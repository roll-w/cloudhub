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

import org.cloudhub.meta.conf.MetaConfigLoader;
import org.cloudhub.meta.server.service.node.HeartbeatServerProperties;
import org.cloudhub.meta.server.service.node.NodeChannelPool;
import org.cloudhub.rpc.GrpcProperties;
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


    @Bean
    public NodeChannelPool nodeChannelPool(GrpcProperties grpcProperties) {
        return new NodeChannelPool(grpcProperties);
    }
}
