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

package org.cloudhub.file.fs.container;

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

    public boolean contains(int index) {
        return index >= start && index <= end;
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
