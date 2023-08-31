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

import org.cloudhub.server.conf.AbstractConfigLoader;
import org.cloudhub.meta.server.MetaServerApplication;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author RollW
 */
public class MetaConfigLoader extends AbstractConfigLoader {
    public static final String RPC_PORT_DEFAULT = "7031";
    public static final String RPC_MAX_INBOUND_SIZE_DEFAULT = "40";

    public static final String FILE_DATA_PATH_DEFAULT = "data";
    public static final String FILE_TEMP_PATH_DEFAULT = "tmp";
    public static final String FILE_UPLOAD_BLOCK_SIZE_DEFAULT = "64";
    public static final String HEARTBEAT_STANDARD_PERIOD_DEFAULT = "500";
    public static final String HEARTBEAT_TIMEOUT_CYCLE_DEFAULT = "3";

    public MetaConfigLoader(InputStream inputStream) throws IOException {
        super(inputStream);
    }

    public int getRpcPort() {
        return getInt(MetaConfigKeys.RPC_PORT, RPC_PORT_DEFAULT);
    }

    public String getFileDataPath() {
        return get(MetaConfigKeys.FILE_DATA_PATH, FILE_DATA_PATH_DEFAULT);
    }

    public String getFileTempPath() {
        return get(MetaConfigKeys.FILE_TEMP_PATH, FILE_TEMP_PATH_DEFAULT);
    }

    public int getRpcMaxInboundSize() {
        return getInt(MetaConfigKeys.RPC_MAX_INBOUND_SIZE,
                RPC_MAX_INBOUND_SIZE_DEFAULT);
    }

    public int getHeartbeatStandardPeriod() {
        return getInt(MetaConfigKeys.HEARTBEAT_STANDARD_PERIOD, HEARTBEAT_STANDARD_PERIOD_DEFAULT);
    }

    public int getHeartbeatTimeoutCycle() {
        return getInt(MetaConfigKeys.HEARTBEAT_TIMEOUT_CYCLE, HEARTBEAT_TIMEOUT_CYCLE_DEFAULT);
    }

    public int getUploadBlockSize() {
        return getInt(MetaConfigKeys.FILE_UPLOAD_BLOCK_SIZE, FILE_UPLOAD_BLOCK_SIZE_DEFAULT);
    }

    @Override
    public String getLogLevel() {
        return get(MetaConfigKeys.LOG_LEVEL, LOG_LEVEL_DEFAULT);
    }

    @Override
    public String getLogPath() {
        return get(MetaConfigKeys.LOG_PATH, LOG_PATH_DEFAULT);
    }

    public static MetaConfigLoader tryOpenDefault() throws IOException {
        return new MetaConfigLoader(
                openConfigInput(MetaServerApplication.class, null));
    }

    public static MetaConfigLoader tryOpenDefault(String path) throws IOException {
        return new MetaConfigLoader(
                openConfigInput(MetaServerApplication.class, path));
    }
}
