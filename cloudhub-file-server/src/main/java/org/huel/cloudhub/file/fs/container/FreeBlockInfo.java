package org.huel.cloudhub.file.fs.container;

import java.util.Objects;

/**
 * @author RollW
 */
public final class FreeBlockInfo {
    private final int start;
    private final int count;
    private final int end;


    public FreeBlockInfo(int start, int end) {
        this.start = start;
        this.end = end;
        this.count = end - start + 1;
    }

    public boolean checkInvalid() {
        return count <= 0;
    }

    public int getStart() {
        return start;
    }

    public int getCount() {
        return count;
    }

    public int getEnd() {
        return end;
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

    @Override
    public String toString() {
        return "{FreeBlock[%d-%d][%d]}".formatted(start, end, count);
    }
}
