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

package org.cloudhub.file.conf;

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
