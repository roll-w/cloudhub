package org.huel.cloudhub.client.conf;

import org.huel.cloudhub.conf.AbstractConfigLoader;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author RollW
 */
public class ClientConfigLoader extends AbstractConfigLoader {
    public static final int RPC_PORT_DEFAULT = 7011;
    public static final int RPC_MAX_INBOUND_SIZE_DEFAULT = 40;

    public static final int WEB_PORT_DEFAULT = 7010;
    public static final String FILE_TEMP_PATH_DEFAULT = "tmp/tmp";

    public ClientConfigLoader(InputStream inputStream) throws IOException {
        super(inputStream);
    }

    public int getRpcPort() {
        return getInt(ClientConfigKeys.RPC_PORT, RPC_PORT_DEFAULT);
    }

    public int getRpcPort(int defaultPort) {
        return getInt(ClientConfigKeys.RPC_PORT, defaultPort);
    }

    public int getWebPort() {
        return getInt(ClientConfigKeys.WEB_PORT, WEB_PORT_DEFAULT);
    }

    public int getWebPort(int defaultPort) {
        return getInt(ClientConfigKeys.WEB_PORT, defaultPort);
    }

    public String getTempFilePath() {
        return get(ClientConfigKeys.FILE_TEMP_PATH, FILE_TEMP_PATH_DEFAULT);
    }

    public int getRpcMaxInboundSize() {
        return getInt(ClientConfigKeys.RPC_MAX_INBOUND_SIZE,
                RPC_MAX_INBOUND_SIZE_DEFAULT);
    }

    public String getMetaServerAddress() {
        return get(ClientConfigKeys.META_ADDRESS, null);
    }

    public String getDatabaseUrl() {
        return get(ClientConfigKeys.DATABASE_URL, null);
    }

    public String getDatabaseUsername() {
        return get(ClientConfigKeys.DATABASE_USERNAME, null);
    }

    public String getDatabasePassword() {
        return get(ClientConfigKeys.DATABASE_PASSWORD, null);
    }

    public static ClientConfigLoader tryOpenDefault(Class<?> loader) throws IOException {
        return new ClientConfigLoader(
                openConfigInput(loader)
        );
    }
}
