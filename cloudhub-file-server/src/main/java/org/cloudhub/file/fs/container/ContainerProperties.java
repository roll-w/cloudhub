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

package org.cloudhub.file.fs.container;

import java.io.File;

/**
 * Container Properties.
 *
 * @author RollW
 */
public class ContainerProperties {
    public static final String CONTAINER_PATH = "data";
    public static final String META_PATH = "meta";

    private final String stagePath;

    private final String filePath;
    /**
     * block size, in kb
     */
    private int blockSize;

    /**
     * block count
     */
    private int blockCount;

    public ContainerProperties(String filePath, String stagePath,
                               int blockSize, int blockCount) {
        this.filePath = filePath;
        this.stagePath = stagePath;
        this.blockSize = blockSize;
        this.blockCount = blockCount;
    }

    public String getContainerPath() {
        return filePath + File.separator + CONTAINER_PATH;
    }

    public String getMetaPath() {
        return filePath + File.separator + META_PATH;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getStagePath() {
        return stagePath;
    }

    public int getBlockSize() {
        return blockSize;
    }

    public void setBlockSize(int blockSize) {
        this.blockSize = blockSize;
    }

    public int getBlockCount() {
        return blockCount;
    }

    public void setBlockCount(int blockCount) {
        this.blockCount = blockCount;
    }

    public int getBlockSizeInBytes() {
        return blockSize * 1024;
    }

    public long getContainerSizeBytes() {
        return (long) blockCount * getBlockSizeInBytes();
    }
}
