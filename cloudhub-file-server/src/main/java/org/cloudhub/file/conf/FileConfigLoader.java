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

import org.cloudhub.file.server.FileServerApplication;
import org.cloudhub.conf.AbstractConfigLoader;
import org.cloudhub.file.server.FileServerApplication;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author RollW
 */
public class FileConfigLoader extends AbstractConfigLoader {
    public static final String RPC_PORT_DEFAULT = "7021";
    public static final String RPC_MAX_INBOUND_SIZE_DEFAULT = "40";

    public static final String CONTAINER_BLOCK_SIZE_DEFAULT = "64";
    public static final String CONTAINER_BLOCK_NUM_DEFAULT = "1024";

    public static final String FILE_STAGING_PATH_DEFAULT = "tmp/staging";
    public static final String FILE_STORE_PATH_DEFAULT = "dfs";

    public static final String LOG_LEVEL_DEFAULT = "info";
    public static final String LOG_PATH_DEFAULT = "console";

    public FileConfigLoader(InputStream inputStream) throws IOException {
        super(inputStream);
    }

    public int getRpcPort() {
        return getInt(FileConfigKeys.RPC_PORT, RPC_PORT_DEFAULT);
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

    public String getLogLevel() {
        return get(FileConfigKeys.LOG_LEVEL, LOG_LEVEL_DEFAULT);
    }

    public String getLogPath() {
        return get(FileConfigKeys.LOG_PATH, LOG_PATH_DEFAULT);
    }

    public static FileConfigLoader tryOpenDefault() throws IOException {
        return new FileConfigLoader(
                openConfigInput(FileServerApplication.class));
    }
}
