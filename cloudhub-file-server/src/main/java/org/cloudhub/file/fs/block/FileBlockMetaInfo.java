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

package org.cloudhub.file.fs.block;

import java.util.Comparator;
import java.util.List;

/**
 * @author RollW
 */
public class FileBlockMetaInfo {
    private final String fileId;
    private final long fileLength;
    private final long validBytes;
    private final List<BlockMetaInfo> blockMetaInfos;
    private final long[] serials;

    public FileBlockMetaInfo(String fileId,
                             List<BlockMetaInfo> blockMetaInfos,
                             long blockSizeInBytes, long validBytes) {
        this.fileId = fileId;
        this.blockMetaInfos = sortBlockMetas(blockMetaInfos);
        this.validBytes = validBytes;
        this.fileLength = calcFileLength(blockMetaInfos, blockSizeInBytes);
        this.serials = calcSerials(blockMetaInfos);
    }

    private List<BlockMetaInfo> sortBlockMetas(List<BlockMetaInfo> blockMetaInfos) {
        return blockMetaInfos.stream()
                .sorted(Comparator.comparingLong(BlockMetaInfo::getContainerSerial))
                .toList();
    }

    private long[] calcSerials(List<BlockMetaInfo> blockMetaInfos) {
        if (blockMetaInfos.isEmpty()) {
            return new long[0];
        }
        long[] serials = new long[blockMetaInfos.size()];
        int index = 0;
        for (BlockMetaInfo blockMetaInfo : blockMetaInfos) {
            serials[index++] = blockMetaInfo.getContainerSerial();
        }
        return serials;
    }

    private long calcFileLength(List<BlockMetaInfo> blockMetaInfos, long blockSizeInBytes) {
        long res = 0;
        for (BlockMetaInfo blockMetaInfo : blockMetaInfos) {
            if (blockMetaInfo.getNextContainerSerial() == BlockMetaInfo.NOT_CROSS_CONTAINER) {
                res += blockMetaInfo.occupiedBlocks() * blockSizeInBytes;
                continue;
            }
            res += (blockMetaInfo.occupiedBlocks() - 1) * blockSizeInBytes +
                    blockMetaInfo.getValidBytes();
        }
        return res;
    }

    public long getValidBytes() {
        return validBytes;
    }

    public String getFileId() {
        return fileId;
    }

    public List<BlockMetaInfo> getAfter(long serial) {
        return blockMetaInfos.stream()
                .filter(blockMetaInfo ->
                        blockMetaInfo.getContainerSerial() >= serial)
                .toList();
    }

    public int getBlocksCount() {
        int sum = 0;
        for (BlockMetaInfo blockMetaInfo : blockMetaInfos) {
            sum += blockMetaInfo.occupiedBlocks();
        }
        return sum;
    }

    public BlockMetaInfo getBlockMetaInfoAt(long serial) {
        // binary search.
        int low = 0;
        int high = blockMetaInfos.size() - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            BlockMetaInfo midVal = blockMetaInfos.get(mid);
            int cmp = Long.compare(midVal.getContainerSerial(), serial);
            if (cmp < 0) {
                low = mid + 1;
            } else if (cmp > 0) {
                high = mid - 1;
            } else {
                return midVal; // found value
            }
        }
        return null;
    }

    public long[] getSerials() {
        return serials;
    }

    public List<BlockMetaInfo> getBlockMetaInfos() {
        return blockMetaInfos;
    }

    public long getFileLength() {
        return fileLength;
    }
}
