package org.huel.cloudhub.meta.server;

import io.grpc.Server;
import org.huel.cloudhub.meta.conf.MetaConfigLoader;
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

    private static final String LOG_FILE = "cloudhub-file-server.out";
    private static final String ARCHIVE_LOG_FILE = "cloudhub-file-server-log.%d{yyyy-MM-dd}.%i.log";

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
