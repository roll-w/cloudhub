package org.huel.cloudhub.client.conf;

/**
 * Client configuration keys.
 *
 * @author RollW
 */
public class ClientConfigKeys {
    /**
     * Cloudhub client rpc port.
     * receive remote call through the port.
     */
    public static final String RPC_PORT = "cloudhub.rpc.port";

    /**
     * Cloudhub client rpc max inbound size in mb.
     */
    public static final String RPC_MAX_INBOUND_SIZE = "cloudhub.rpc.max_inbound_size";

    /**
     * Cloudhub web port.
     * Access client in web through the port.
     */
    public static final String WEB_PORT = "cloudhub.web.port";

    /**
     * Meta-server address.
     * Format: host:port.
     */
    public static final String META_ADDRESS = "cloudhub.meta.address";

    /**
     * Cloudhub client temporary file directory.
     */
    public static final String FILE_TEMP_PATH = "cloudhub.file.temp_dir";

    public static final String DATABASE_URL = "cloudhub.client.db.url";

    public static final String DATABASE_USERNAME = "cloudhub.client.db.username";

    public static final String DATABASE_PASSWORD = "cloudhub.client.db.password";

    private ClientConfigKeys() {
    }
}
