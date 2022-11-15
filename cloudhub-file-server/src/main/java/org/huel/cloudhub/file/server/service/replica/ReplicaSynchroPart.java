package org.huel.cloudhub.file.server.service.replica;

import org.huel.cloudhub.file.fs.block.BlockGroupsInfo;
import org.huel.cloudhub.file.fs.container.Container;

/**
 * @author RollW
 */
public class ReplicaSynchroPart {
    private final Container container;
    private final BlockGroupsInfo blockGroupsInfo;
    private final int count;

    public ReplicaSynchroPart(Container container, BlockGroupsInfo blockGroupsInfo, int count) {
        this.container = container;
        this.blockGroupsInfo = blockGroupsInfo;
        this.count = count;
    }

    public Container getContainer() {
        return container;
    }

    public BlockGroupsInfo getBlockGroupsInfo() {
        return blockGroupsInfo;
    }

    public int getCount() {
        return count;
    }
}
