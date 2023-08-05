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

package org.cloudhub.meta.conf;

/**
 * @author RollW
 */
public class MetaConfigKeys {
    /**
     * Cloudhub meta-server rpc port.
     * Receive remote call through the port.
     */
    public static final String RPC_PORT = "cloudhub.meta.rpc.port";

    /**
     * Cloudhub meta-server rpc max inbound size in mb.
     */
    public static final String RPC_MAX_INBOUND_SIZE = "cloudhub.meta.rpc.max-inbound-size";

    /**
     * Cloudhub meta-server file data directory.
     */
    public static final String FILE_DATA_PATH = "cloudhub.meta.data-dir";

    /**
     * Cloudhub meta-server file temp directory.
     */
    public static final String FILE_TEMP_PATH = "cloudhub.meta.temp-dir";

    /**
     * Cloudhub meta-server file upload block size, in kb.
     */
    public static final String FILE_UPLOAD_BLOCK_SIZE = "cloudhub.meta.upload-block-size";

    /**
     * Cloudhub standard heartbeat period in ms.
     * Will send to file servers.
     */
    public static final String HEARTBEAT_STANDARD_PERIOD = "cloudhub.file.heartbeat.standard-period";

    /**
     * Cloudhub timeout cycles for the heartbeat period.
     * <p>
     * For example: when the heartbeat period is 200ms and
     * the timeout cycle is set to 2, the following effect will occur:
     * when the last heartbeat to the now interval exceeds 400ms,
     * the server will be removed from the active servers list.
     */
    public static final String HEARTBEAT_TIMEOUT_CYCLE = "cloudhub.file.heartbeat.timeout-cycle";

    /**
     * Cloudhub meta-server log level. Support: trace, debug, info, warn, error.
     */
    public static final String LOG_LEVEL = "cloudhub.meta.log.level";

    /**
     * The log path.
     * If the path is "console," log will be printed to console.
     * Or will be saved to the path as file.
     * File name format is
     * "cloudhub-meta-server.out" & "cloudhub-meta-server-{date}.{order}.log".
     * <p>
     * When not start as daemon, this config will be ignored and
     * log will be printed to console.
     */
    public static final String LOG_PATH = "cloudhub.meta.log.path";

    private MetaConfigKeys() {
    }
}
