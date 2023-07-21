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

package org.cloudhub.server;

import oshi.hardware.GlobalMemory;

/**
 * @author RollW
 */
public class MemoryUsageInfo {
    private long total;
    private long used;
    private long free;

    public MemoryUsageInfo(long total, long used, long free) {
        this.total = total;
        this.used = used;
        this.free = free;
    }

    private MemoryUsageInfo() {
    }

    public long getTotal() {
        return total;
    }

    public long getUsed() {
        return used;
    }

    public long getFree() {
        return free;
    }

    public MemoryUsageInfo fork() {
        return new MemoryUsageInfo(total, used, free);
    }

    protected MemoryUsageInfo reload(GlobalMemory memory) {
        this.total = memory.getTotal();
        this.free = memory.getAvailable();
        this.used = memory.getTotal() - memory.getAvailable();
        return this;
    }


    public static MemoryUsageInfo load(GlobalMemory memory) {
        return new MemoryUsageInfo().reload(memory);
    }
}
