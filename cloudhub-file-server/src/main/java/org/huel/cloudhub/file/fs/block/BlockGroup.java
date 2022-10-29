package org.huel.cloudhub.file.fs.block;

import org.huel.cloudhub.file.fs.meta.SerializedBlockGroup;

/**
 * @author RollW
 */
public class BlockGroup {
    private final int start;
    private final int end;

    public BlockGroup(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public int start() {
        return start;
    }

    public int end() {
        return end;
    }

    public int occupiedBlocks() {
        return end - start + 1;
    }

    public boolean contains(int index) {
        return index >= start && index <= end;
    }

    public SerializedBlockGroup serialize() {
        return SerializedBlockGroup.newBuilder()
                .setStart(start)
                .setEnd(end)
                .build();
    }

    public static BlockGroup deserialize(SerializedBlockGroup serializedBlockGroup) {
        return new BlockGroup(serializedBlockGroup.getStart(),
                serializedBlockGroup.getEnd());
    }
}
