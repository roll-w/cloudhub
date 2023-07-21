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

package org.cloudhub.meta.server.configuration;

/**
 * @author RollW
 */
public class FileProperties {
    private final String dataPath;

    private final String tempFilePath;
    /**
     * Upload block size, in kb
     */
    private final int blockSize;


    public FileProperties(String dataPath, String tempFilePath, int blockSize) {
        this.dataPath = dataPath;
        this.tempFilePath = tempFilePath;
        this.blockSize = blockSize;
    }

    public String getDataPath() {
        return dataPath;
    }

    public String getTempFilePath() {
        return tempFilePath;
    }

    public int getBlockSize() {
        return blockSize;
    }

    public int getBlockSizeInBytes() {
        return blockSize * 1024;
    }
}
