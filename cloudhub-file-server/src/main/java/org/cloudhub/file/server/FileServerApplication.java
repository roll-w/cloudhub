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

package org.cloudhub.file.server;

import io.grpc.Server;
import org.cloudhub.file.conf.FileConfigLoader;
import org.cloudhub.file.server.service.heartbeat.HeartbeatTask;
import org.cloudhub.server.ApplicationHelper;
import org.cloudhub.server.ServerInitializeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ConfigurableApplicationContext;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * File server start class.
 *
 * @author RollW
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class FileServerApplication {
    private static final Logger logger = LoggerFactory.getLogger(FileServerApplication.class);

    private final Server server;

    public FileServerApplication(ConfigurableApplicationContext context,
                                 Server server,
                                 HeartbeatTask heartbeatTask) {
        FileServerApplication.sContext = context;
        this.server = server;
        this.heartbeatTask = heartbeatTask;
    }

    private static ConfigurableApplicationContext sContext;

    public static void main(String[] args) throws Exception {
        try {
            sContext = ApplicationHelper.startApplication(
                    FileServerApplication.class,
                    "file-server",
                    FileConfigLoader::tryOpenDefault, args
            );
        } catch (ServerInitializeException e) {
            logger.error("Start file-server failed.", e);
            throw e;
        }
        logger.info("Cloudhub file-server exit at {}.",
                LocalDateTime.now().format(
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                )
        );
    }

    private final HeartbeatTask heartbeatTask;

    public static void exit(int exitCode) {
        int finalCode = SpringApplication.exit(sContext, () -> exitCode);
        System.exit(finalCode);
    }

    @PostConstruct
    public void startSendHeartbeat() {
        heartbeatTask.startSendHeartbeat();
    }

    @PreDestroy
    public void stopSendHeartbeat() {
        heartbeatTask.stop();
    }

    @PostConstruct
    public void startServer() throws IOException {
        server.start();
    }

    @PreDestroy
    public void stopServer() {
        server.shutdown();
    }
}
