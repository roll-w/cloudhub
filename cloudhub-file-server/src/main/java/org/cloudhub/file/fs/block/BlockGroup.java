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

package org.cloudhub.file.fs.block;

import org.cloudhub.file.fs.meta.SerializedBlockGroup;

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
