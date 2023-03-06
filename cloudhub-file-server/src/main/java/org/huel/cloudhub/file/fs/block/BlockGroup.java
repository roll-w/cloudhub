package org.huel.cloudhub.file.fs.block;

import org.huel.cloudhub.file.fs.meta.SerializedBlockGroup;

import java.util.Objects;

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

    @Deprecated
    public SerializedBlockGroup serialize() {
        return SerializedBlockGroup.newBuilder()
                .setStart(start)
                .setEnd(end)
                .build();
    }

    @Deprecated
    public static BlockGroup deserialize(SerializedBlockGroup serializedBlockGroup) {
        return new BlockGroup(serializedBlockGroup.getStart(),
                serializedBlockGroup.getEnd());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlockGroup that = (BlockGroup) o;
        return start == that.start && end == that.end;
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }

    @Override
    public String toString() {
        return "{BlockGroup[%d-%d]}".formatted(start, end);
    }
}
