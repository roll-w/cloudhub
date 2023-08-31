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

package org.cloudhub.meta.server;

import io.grpc.Server;
import org.cloudhub.meta.conf.MetaConfigLoader;
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

/**
 * @author RollW
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class MetaServerApplication {
    private static final Logger logger = LoggerFactory.getLogger(MetaServerApplication.class);

    private final Server server;

    private static ConfigurableApplicationContext sContext;

    public MetaServerApplication(Server server, ConfigurableApplicationContext context) {
        this.server = server;
        MetaServerApplication.sContext = context;
    }

    public static void main(String[] args) throws Exception {
        try {
            sContext = ApplicationHelper.startApplication(
                    MetaServerApplication.class,
                    "meta-server",
                    MetaConfigLoader::tryOpenDefault, args
            );
        } catch (ServerInitializeException e) {
            logger.error("Start meta-server failed.", e);
            throw e;
        }
    }

    public static void exit(int exitCode) {
        int finalCode = SpringApplication.exit(sContext, () -> exitCode);
        System.exit(finalCode);
    }

    @PostConstruct
    public void runServer() throws IOException {
        server.start();
    }

    @PreDestroy
    public void stopServer() {
        server.shutdown();
    }
}
