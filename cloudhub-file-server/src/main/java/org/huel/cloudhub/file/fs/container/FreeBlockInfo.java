package org.huel.cloudhub.file.fs.container;

import java.util.Objects;

/**
 * @author RollW
 */
public final class FreeBlockInfo {
    private final int start;
    private final int count;


    public FreeBlockInfo(int start, int count) {
        this.start = start;
        this.count = count;
    }

    public int getStart() {
        return start;
    }

    public int getCount() {
        return count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FreeBlockInfo that = (FreeBlockInfo) o;
        return start == that.start && count == that.count;
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, count);
    }
}
