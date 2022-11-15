package org.huel.cloudhub.file.fs.block;

import java.util.*;

/**
 * @author RollW
 */
public class BlockGroupsInfo {
    private List<BlockGroup> blockGroups = new ArrayList<>();

    public BlockGroupsInfo(Collection<BlockGroup> blockGroups) {
        this.blockGroups.addAll(blockGroups);
        sort();
    }

    private void sort() {
        blockGroups.sort(Comparator.comparingInt(BlockGroup::start));
        blockGroups = mergeBlockGroups(blockGroups);
        if (!blockGroups.isEmpty()) {
            blockGroups.sort(Comparator.comparingInt(BlockGroup::start));
        }
    }

    public List<BlockGroup> getBlockGroups() {
        return Collections.unmodifiableList(blockGroups);
    }

    private List<BlockGroup> mergeBlockGroups(List<BlockGroup> blockGroups) {
        // here the blockGroups have been sorted by start.
        if (blockGroups.isEmpty()) {
            return List.of();
        }
        List<BlockGroup> res = new ArrayList<>();
        BlockGroup first = blockGroups.get(0);
        int l = first.start(), r = first.end(), i = 0;
        for (BlockGroup blockGroup : blockGroups) {
            if (i == 0) {
                i++;
                continue;
            }
            if (blockGroup.start() - 1 > r) {
                res.add(new BlockGroup(l, r));
                l = blockGroup.start();
                r = blockGroup.end();
            } else {
                r = Math.max(r, blockGroup.end());
            }
            i++;
        }
        res.add(new BlockGroup(l, r));
        return res;
    }

    public int getBlocksCountAfter(int blockIndex) {
        List<BlockGroup> subs = getBlockGroupAfter(blockIndex);
        if (subs.isEmpty()) {
            return 0;
        }
        int count = 0;

        for (BlockGroup sub : subs) {
            if (blockIndex >= 0 && sub.contains(blockIndex)) {
                count += sub.end() - blockIndex + 1;
                continue;
            }
            count += sub.occupiedBlocks();
        }
        return count;
    }

    public List<BlockGroup> getBlockGroupAfter(int blockIndex) {
        if (blockGroups.isEmpty()) {
            return List.of();
        }
        if (blockIndex > blockGroups.get(blockGroups.size() - 1).end()) {
            return List.of();
        }
        if (blockIndex < 0 || blockIndex <= blockGroups.get(0).start()) {
            return getBlockGroups();
        }

        int index = 0;
        for (BlockGroup blockGroup : blockGroups) {
            if (blockGroup.contains(blockIndex)) {
                break;
            }
            index++;
        }
        // TODO: binary search.
        final int size = blockGroups.size();
        if (size - 1 == index) {
            return List.of(blockGroupAt(size - 1));
        }
        return blockGroups.subList(index, size - 1);
    }

    public int getBlockGroupsCount() {
        return blockGroups.size();
    }

    public BlockGroup blockGroupAt(int index) {
        return blockGroups.get(index);
    }

    public boolean contains(int index) {
        for (BlockGroup blockGroup : blockGroups) {
            if (blockGroup.contains(index)) {
                return true;
            }
        }
        return false;
    }

    public int occupiedBlocks() {
        int sum = 0;
        for (BlockGroup blockGroup : blockGroups) {
            sum += blockGroup.occupiedBlocks();
        }
        return sum;
    }

    public int[] flatToBlocksIndex() {
        int[] res = new int[occupiedBlocks()];
        int i = 0;
        for (BlockGroup blockGroup : blockGroups) {
            for (int j = blockGroup.start(); j <= blockGroup.end(); j++) {
                res[i] = j;
                i++;
            }
        }
        return res;
    }

}
