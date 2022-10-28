package org.huel.cloudhub.file.fs.container;

import org.huel.cloudhub.file.fs.block.BlockMetaInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author RollW
 */
public class ReplicaContainer {
    private final List<BlockMetaInfo> blockMetaInfos = new ArrayList<>();
    private String sourceServerId;
    // TODO:
}
