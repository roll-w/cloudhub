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
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author RollW
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class MetaServerApplication {
    private final Server server;

    public MetaServerApplication(Server server) {
        this.server = server;
    }

    public static void main(String[] args) throws Exception {
        SpringApplication application =
                new SpringApplication(MetaServerApplication.class);

        MetaConfigLoader loader = MetaConfigLoader.tryOpenDefault();
        String logLevel = loader.getLogLevel();

        Map<String, Object> overrideProperties = new HashMap<>();

        overrideProperties.put("logging.level.org.huel.cloudhub", logLevel);
        overrideProperties.put("logging.level.org.cloudhub", logLevel);

        logToFile(args, overrideProperties, loader);
        application.setDefaultProperties(overrideProperties);
        application.setWebApplicationType(WebApplicationType.NONE);

        application.run();
    }

    private static final String LOG_FILE = "cloudhub-meta-server.out";
    private static final String ARCHIVE_LOG_FILE = "cloudhub-meta-server-log.%d{yyyy-MM-dd}.%i.log";

    private static void logToFile(String[] args,
                                  Map<String, Object> overrideProperties,
                                  MetaConfigLoader configLoader) {
        if (!startAsDaemon(args)) {
            System.out.println("Not start as daemon, log to console.");
            return;
        }
        String logPath = configLoader.getLogPath();
        if (MetaConfigLoader.LOG_PATH_DEFAULT.equals(logPath)) {
            return;
        }

        overrideProperties.put("logging.file.name", logPath + "/" + LOG_FILE);
        overrideProperties.put("logging.logback.rollingpolicy.file-name-pattern",
                logPath + "/" + ARCHIVE_LOG_FILE);
    }

    private static boolean startAsDaemon(String[] args) {
        for (String arg : args) {
            if (arg.equals("--daemon")) {
                return true;
            }
        }
        return false;
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
