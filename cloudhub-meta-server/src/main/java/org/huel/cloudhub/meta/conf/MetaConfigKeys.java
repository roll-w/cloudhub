package org.huel.cloudhub.meta.conf;

/**
 * @author RollW
 */
public class MetaConfigKeys {
    /**
     * Cloudhub meta-server rpc port.
     * receive remote call through the port.
     */
    public static final String RPC_PORT = "cloudhub.rpc.port";

    /**
     * Cloudhub meta-server rpc max inbound size in mb.
     */
    public static final String RPC_MAX_INBOUND_SIZE = "cloudhub.rpc.max_inbound_size";

    /**
     * Cloudhub meta-server file data directory.
     */
    public static final String FILE_DATA_PATH = "cloudhub.file.data_dir";

    /**
     * Cloudhub meta-server file temp directory.
     */
    public static final String FILE_TEMP_PATH = "cloudhub.file.temp_dir";

    /**
     * Cloudhub meta-server file upload block size, in kb.
     */
    public static final String FILE_UPLOAD_BLOCK_SIZE = "cloudhub.file.upload_block_size";

    /**
     * Cloudhub standard heartbeat period in ms.
     * Will sends to file servers.
     */
    public static final String HEARTBEAT_STANDARD_PERIOD = "cloudhub.heartbeat.standard_period";

    /**
     * Cloudhub timeout cycles for the heartbeat period.
     * <p>
     * For example: when the heartbeat period is 200ms and
     * the timeout cycle is set to 2, the following effect will occur:
     * when the last heartbeat to now interval exceeds 400ms,
     * the server will be removed from the active servers list.
     */
    public static final String HEARTBEAT_TIMEOUT_CYCLE = "cloudhub.heartbeat.timeout_cycle";

    private MetaConfigKeys() {
    }
}
