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

package org.cloudhub.file.fs.meta;

import org.cloudhub.file.fs.block.BlockGroupsInfo;
import org.cloudhub.file.fs.block.BlockMetaInfo;
import org.cloudhub.file.fs.block.BlockGroupsInfo;
import org.cloudhub.file.fs.block.BlockMetaInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author RollW
 */
class Helper {
    public static SerializedContainerBlockMeta buildSerializedContainerMeta(
            int blockSize,
            int usedBlock, int blockCapacity,
            String checksum, List<? extends BlockFileMeta> blockFileMetas) {
        List<SerializedBlockFileMeta> serializedBlockFileMetas =
                transformBlockFileMetas(blockFileMetas);
        return SerializedContainerBlockMeta.newBuilder()
                .setCrc(checksum)
                .setBlockCap(blockCapacity)
                .setUsedBlock(usedBlock)
                .setBlockSize(blockSize)
                .addAllBlockMetas(serializedBlockFileMetas)
                .build();
    }

    public static List<? extends BlockFileMeta> extractBlockFileMetas(
            long serial,
            SerializedContainerBlockMeta serializedContainerBlockMeta) {
        return serializedContainerBlockMeta.getBlockMetasList()
                .stream()
                .map(meta ->
                        BlockMetaInfo.deserialize(meta, serial))
                .toList();
    }

    private static List<SerializedBlockFileMeta> transformBlockFileMetas(
            List<? extends BlockFileMeta> blockFileMetas) {
        if (blockFileMetas == null || blockFileMetas.isEmpty()) {
            return List.of();
        }

        List<SerializedBlockFileMeta> serializedBlockFileMetas = new ArrayList<>();
        blockFileMetas.forEach(blockFileMeta -> {
            List<SerializedBlockGroup> serializedBlockGroups =
                    transformBlockGroups(blockFileMeta.getBlockGroups());
            SerializedBlockFileMeta serializedBlockFileMeta = SerializedBlockFileMeta
                    .newBuilder()
                    .setFileId(blockFileMeta.getFileId())
                    .setCrossSerial(blockFileMeta.getCrossContainerSerial())
                    .setEndBlockBytes(blockFileMeta.getEndBlockByteOffset())
                    .addAllBlockGroups(serializedBlockGroups)
                    .build();
            serializedBlockFileMetas.add(serializedBlockFileMeta);
        });
        return serializedBlockFileMetas;
    }

    private static List<SerializedBlockGroup> transformBlockGroups(
            BlockGroupsInfo blockGroupsInfo) {
        List<SerializedBlockGroup> serializedBlockGroups = new ArrayList<>();
        blockGroupsInfo.getBlockGroups().forEach(blockGroup -> {
            SerializedBlockGroup serializedBlockGroup =
                    SerializedBlockGroup.newBuilder()
                            .setStart(blockGroup.start())
                            .setEnd(blockGroup.end())
                            .build();
            serializedBlockGroups.add(serializedBlockGroup);
        });
        return serializedBlockGroups;
    }
}
