package org.huel.cloudhub.file.server;

import io.grpc.Server;
import org.huel.cloudhub.file.conf.FileConfigLoader;
import org.huel.cloudhub.file.server.service.heartbeat.HeartbeatTask;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ConfigurableApplicationContext;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * File server start class.
 *
 * @author RollW
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class FileServerApplication implements ApplicationRunner {
    private final Server server;

    public FileServerApplication(Server server,
                                 HeartbeatTask heartbeatTask) {
        this.server = server;
        this.heartbeatTask = heartbeatTask;
    }

    private static ConfigurableApplicationContext sContext;

    public static void main(String[] args) throws Exception {
        SpringApplication application =
                new SpringApplication(FileServerApplication.class);

        FileConfigLoader loader = FileConfigLoader.tryOpenDefault();
        String logLevel = loader.getLogLevel();

        Map<String, Object> overrideProperties = new HashMap<>();

        overrideProperties.put("logging.level.org.huel.cloudhub", logLevel);
        overrideProperties.put("logging.level.org.cloudhub", logLevel);

        logToFile(args, overrideProperties, loader);
        application.setDefaultProperties(overrideProperties);
        application.setWebApplicationType(WebApplicationType.NONE);

        sContext = application.run(args);
    }

    private static final String LOG_FILE = "cloudhub-file-server.out";
    private static final String ARCHIVE_LOG_FILE = "cloudhub-file-server-log.%d{yyyy-MM-dd}.%i.log";

    private static void logToFile(String[] args,
                                  Map<String, Object> overrideProperties,
                                  FileConfigLoader configLoader) {
        if (!startAsDaemon(args)) {
            System.out.println("Not start as daemon, log to console.");
            return;
        }
        String logPath = configLoader.getLogPath();
        if (FileConfigLoader.LOG_PATH_DEFAULT.equals(logPath)) {
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

    private final HeartbeatTask heartbeatTask;

    public static void exit(int exitCode) {
        SpringApplication.exit(sContext, () -> exitCode);
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

    @Override
    public void run(ApplicationArguments args) {
    }

}
