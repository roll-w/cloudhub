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

package org.cloudhub.file.server.configuration;

import org.cloudhub.file.conf.FileConfigKeys;
import org.cloudhub.file.conf.FileConfigLoader;
import org.cloudhub.file.fs.container.ContainerProperties;
import org.cloudhub.file.server.service.ClientFileServerChannelPool;
import org.cloudhub.conf.ConfigurationException;
import org.cloudhub.file.conf.FileConfigKeys;
import org.cloudhub.file.conf.FileConfigLoader;
import org.cloudhub.file.fs.container.ContainerProperties;
import org.cloudhub.file.server.service.ClientFileServerChannelPool;
import org.cloudhub.file.server.service.heartbeat.HeartbeatHostProperties;
import org.cloudhub.rpc.GrpcProperties;
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

    @Bean
    public ClientFileServerChannelPool fileServerChannelPool() {
        return new ClientFileServerChannelPool(grpcProperties());
    }
}
