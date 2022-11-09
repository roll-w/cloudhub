package org.huel.cloudhub.file.conf;

import org.huel.cloudhub.conf.AbstractConfigLoader;
import org.huel.cloudhub.file.server.FileServerApplication;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author RollW
 */
public class FileConfigLoader extends AbstractConfigLoader {
    public static final String RPC_PORT_DEFAULT = "7021";
    public static final String RPC_MAX_INBOUND_SIZE_DEFAULT = "40";

    public static final String WEB_PORT_DEFAULT = "7020";

    public static final String CONTAINER_BLOCK_SIZE_DEFAULT = "64";
    public static final String CONTAINER_BLOCK_NUM_DEFAULT = "1024";

    public static final String FILE_STAGING_PATH_DEFAULT = "tmp/staging";
    public static final String FILE_STORE_PATH_DEFAULT = "dfs";

    public FileConfigLoader(InputStream inputStream) throws IOException {
        super(inputStream);
    }

    public int getRpcPort() {
        return getInt(FileConfigKeys.RPC_PORT, RPC_PORT_DEFAULT);
    }

    public int getWebPort() {
        return getInt(FileConfigKeys.WEB_PORT, WEB_PORT_DEFAULT);
    }

    public String getStagingFilePath() {
        return get(FileConfigKeys.FILE_STAGING_PATH, FILE_STAGING_PATH_DEFAULT);
    }

    public String getFileStorePath() {
        return get(FileConfigKeys.FILE_STORE_PATH, FILE_STORE_PATH_DEFAULT);
    }

    public int getBlockSize() {
        return getInt(FileConfigKeys.CONTAINER_BLOCK_SIZE,
                CONTAINER_BLOCK_SIZE_DEFAULT);
    }

    public int getBlockNum() {
        return getInt(FileConfigKeys.CONTAINER_BLOCK_NUM,
                CONTAINER_BLOCK_NUM_DEFAULT);
    }

    public int getRpcMaxInboundSize() {
        return getInt(FileConfigKeys.RPC_MAX_INBOUND_SIZE,
                RPC_MAX_INBOUND_SIZE_DEFAULT);
    }

    public String getMetaServerAddress() {
        return get(FileConfigKeys.META_ADDRESS, null);
    }

    public static FileConfigLoader tryOpenDefault() throws IOException {
        return new FileConfigLoader(
                openConfigInput(FileServerApplication.class));
    }
}
