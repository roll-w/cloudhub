package org.huel.cloudhub.file.conf;

import org.huel.cloudhub.file.server.FileServerApplication;

import java.io.*;
import java.util.Properties;

/**
 * @author RollW
 */
public class ConfigLoader {
    public static final String RPC_PORT_DEFAULT = "7021";
    public static final String RPC_MAX_INBOUND_SIZE_DEFAULT = "40";

    public static final String WEB_PORT_DEFAULT = "7020";

    public static final String CONTAINER_BLOCK_SIZE_DEFAULT = "64";
    public static final String CONTAINER_BLOCK_NUM_DEFAULT = "1024";

    public static final String FILE_STAGING_PATH_DEFAULT = "tmp/staging";
    public static final String FILE_STORE_PATH_DEFAULT = "dfs";

    private final Properties properties;

    public ConfigLoader(InputStream inputStream) throws IOException {
        properties = new Properties();
        properties.load(inputStream);
    }

    public String get(String key) {
        return properties.getProperty(key, null);
    }

    public String get(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public int getRpcPort() {
        return Integer.parseInt(get(ConfigKeys.RPC_PORT, RPC_PORT_DEFAULT));
    }

    public int getWebPort() {
        return Integer.parseInt(get(ConfigKeys.WEB_PORT, WEB_PORT_DEFAULT));
    }

    public String getStagingFilePath() {
        return get(ConfigKeys.FILE_STAGING_PATH, FILE_STAGING_PATH_DEFAULT);
    }

    public String getFileStorePath() {
        return get(ConfigKeys.FILE_STORE_PATH, FILE_STORE_PATH_DEFAULT);
    }

    public int getBlockSize() {
        return Integer.parseInt(get(ConfigKeys.CONTAINER_BLOCK_SIZE,
                CONTAINER_BLOCK_SIZE_DEFAULT));
    }

    public int getBlockNum() {
        return Integer.parseInt(get(ConfigKeys.CONTAINER_BLOCK_NUM,
                CONTAINER_BLOCK_NUM_DEFAULT));
    }

    public int getRpcMaxInboundSize() {
        return Integer.parseInt(get(ConfigKeys.RPC_MAX_INBOUND_SIZE,
                RPC_MAX_INBOUND_SIZE_DEFAULT));
    }

    public String getMetaServerAddress() {
        return get(ConfigKeys.META_ADDRESS, null);
    }

    public static ConfigLoader tryOpenDefault() throws IOException {
        return new ConfigLoader(openConfigInput());
    }

    private static InputStream openConfigInput() throws IOException {
        File confFile = tryFile();
        if (!confFile.exists()) {
            return FileServerApplication.class.getResourceAsStream("/cloudhub.conf");
        }
        return new FileInputStream(confFile);
    }

    private static File tryFile() {
        File confFile = new File("conf/cloudhub.conf");
        if (confFile.exists()) {
            return confFile;
        }
        return new File("cloudhub.conf");
    }
}
