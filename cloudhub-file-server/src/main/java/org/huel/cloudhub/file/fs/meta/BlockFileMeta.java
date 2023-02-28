package org.huel.cloudhub.file.fs.meta;

import org.huel.cloudhub.file.fs.block.BlockGroupsInfo;

/**
 * @author RollW
 */
public interface BlockFileMeta {
    String getFileId();

    BlockGroupsInfo getBlockGroups();

    long getEndBlockByteOffset();

    long getCrossContainerSerial();

    long getContainerSerial();
}
