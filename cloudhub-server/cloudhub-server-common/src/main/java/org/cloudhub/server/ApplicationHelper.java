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

package org.cloudhub.server;

import org.cloudhub.server.conf.AbstractConfigLoader;
import org.cloudhub.server.conf.ConfigLoaderProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.context.ConfigurableApplicationContext;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * @author RollW
 */
public final class ApplicationHelper {

    public static final String CONFIG_LOADER_KEY = "cloudhub.config.loader";

    public static final String CONFIG_PATH = "--config";
    public static final String DAEMON = "--daemon";

    public static final String SHORTAGE_CONFIG_PATH = "-c";
    public static final String SHORTAGE_DAEMON = "-d";

    public static ConfigurableApplicationContext startApplication(
            Class<?> applicationClazz, String artifactId,
            ConfigLoaderProvider<?> configLoaderProvider,
            String[] args) throws ServerInitializeException {
        try {
            return startApplicationThrows(applicationClazz, artifactId,
                    configLoaderProvider, args);
        } catch (ServerInitializeException e) {
            throw e;
        } catch (Exception e) {
            throw new ServerInitializeException(e);
        }
    }

    private static ConfigurableApplicationContext startApplicationThrows(
            Class<?> applicationClazz, String artifactId,
            ConfigLoaderProvider<?> configLoaderProvider,
            String[] args) throws Exception {
        SpringApplication application =
                new SpringApplication(applicationClazz);

        String initConfigPath = getConfigPath(args);
        AbstractConfigLoader loader =
                configLoaderProvider.provide(initConfigPath);

        Map<String, Object> overrideProperties = new HashMap<>();

        overrideProperties.put("logging.level.org.cloudhub", loader.getLogLevel());
        overrideProperties.put(CONFIG_LOADER_KEY, loader);

        logToFile(artifactId, args, overrideProperties, loader);
        application.setDefaultProperties(overrideProperties);
        application.setWebApplicationType(WebApplicationType.NONE);

        return application.run();
    }


    private static final String LOG_FILE = "cloudhub-{0}.out";
    private static final String ARCHIVE_LOG_FILE = "cloudhub-{0}-log.%d'{yyyy-MM-dd}'.%i.log";


    private static String getLogFileOf(String artifactId) {
        return MessageFormat.format(LOG_FILE, artifactId);
    }

    private static String getArchiveLogFileOf(String artifactId) {
        return MessageFormat.format(ARCHIVE_LOG_FILE, artifactId);
    }

    private static void logToFile(String artifactId,
                                  String[] args,
                                  Map<String, Object> overrideProperties,
                                  AbstractConfigLoader configLoader) {
        if (!startAsDaemon(args)) {
            System.out.println("Not start as daemon, log to console.");
            return;
        }
        String logPath = configLoader.getLogPath();
        if (AbstractConfigLoader.LOG_PATH_DEFAULT.equals(logPath)) {
            return;
        }

        overrideProperties.put("logging.file.name", logPath + "/" + getLogFileOf(artifactId));
        overrideProperties.put("logging.logback.rollingpolicy.file-name-pattern",
                logPath + "/" + getArchiveLogFileOf(artifactId));
    }

    private static boolean startAsDaemon(String[] args) {
        for (String arg : args) {
            if (arg.equals(DAEMON) || arg.equals(SHORTAGE_DAEMON)) {
                return true;
            }
        }
        return false;
    }

    private static String getConfigPath(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals(CONFIG_PATH) || args[i].equals(SHORTAGE_CONFIG_PATH)) {
                if (i + 1 >= args.length) {
                    throw configPathNotSpecified(args[i]);
                }
                return args[i + 1];
            }
        }
        return null;
    }

    private static ServerInitializeException configPathNotSpecified(String argName) {
        return new ServerInitializeException("You are using " +
                "config file path option: '" + argName +
                "', but not specify the config file path.");
    }


    private ApplicationHelper() {
    }
}
