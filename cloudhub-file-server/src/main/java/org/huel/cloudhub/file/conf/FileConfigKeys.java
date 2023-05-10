package org.huel.cloudhub.file.conf;

/**
 * File-server configuration keys.
 *
 * @author RollW
 */
public class FileConfigKeys {
    /**
     * Cloudhub file-server rpc port.
     * receive remote call through the port.
     */
    public static final String RPC_PORT = "cloudhub.rpc.port";
    /**
     * Cloudhub file-server rpc max inbound size in mb.
     */
    public static final String RPC_MAX_INBOUND_SIZE = "cloudhub.rpc.max_inbound_size";

    /**
     * Meta-server address.
     * Format: host:port.
     */
    public static final String META_ADDRESS = "cloudhub.meta.address";

    /**
     * Cloudhub file-server file store directory.
     */
    public static final String FILE_STORE_PATH = "cloudhub.file.store_dir";

    /**
     * Cloudhub file-server file staging directory.
     */
    public static final String FILE_STAGING_PATH = "cloudhub.file.staging_dir";

    /**
     * Block size in kb.
     */
    public static final String CONTAINER_BLOCK_SIZE = "cloudhub.file.cont.block_size";

    /**
     * How many blocks are contained in the container.
     */
    public static final String CONTAINER_BLOCK_NUM = "cloudhub.file.cont.block_num";

    /**
     * Cloudhub file-server log level. Support: trace, debug, info, warn, error.
     */
    public static final String LOG_LEVEL = "cloudhub.file.log.level";

    /**
     * Log path. If path is "console", log will be printed to console.
     * Or will be saved to the path as file. File name format is
     * "cloudhub-file-server.out" & "cloudhub-file-server-{date}.{order}.log".
     * <p>
     * When not start as daemon, this config will be ignored and
     * log will be printed to console.
     */
    public static final String LOG_PATH = "cloudhub.file.log.path";

    private FileConfigKeys() {
    }
}
