package org.huel.cloudhub.file.fs.meta;

import org.huel.cloudhub.file.fs.block.BlockGroupsInfo;

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
