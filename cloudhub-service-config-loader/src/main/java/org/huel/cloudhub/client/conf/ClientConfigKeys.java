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

    /**
     * Cloudhub client log level. Support: trace, debug, info, warn, error.
     */
    public static final String LOG_LEVEL = "cloudhub.client.log.level";

    /**
     * Log path. If path is "console", log will be printed to console.
     * Or will be saved to the path as file. File name format is
     * "cloudhub-disk-client.out" & "cloudhub-disk-client-{date}.{order}.log".
     * <p>
     * When not start as daemon, this config will be ignored and
     * log will be printed to console.
     */
    public static final String LOG_PATH = "cloudhub.client.log.path";

    public static final String DATABASE_URL = "cloudhub.client.db.url";

    public static final String DATABASE_USERNAME = "cloudhub.client.db.username";

    public static final String DATABASE_PASSWORD = "cloudhub.client.db.password";


    private ClientConfigKeys() {
    }
}
