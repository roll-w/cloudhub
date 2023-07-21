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

/**
 * @author RollW
 */
public class JvmUsageInfo {
    private long total;
    private long max;
    private long free;

    public JvmUsageInfo(long total, long max, long free) {
        this.total = total;
        this.max = max;
        this.free = free;
    }

    public long getTotal() {
        return total;
    }

    public long getMax() {
        return max;
    }

    public long getFree() {
        return free;
    }

    public long getUsed() {
        return total - free;
    }

    public JvmUsageInfo fork() {
        return new JvmUsageInfo(total, max, free);
    }

    protected JvmUsageInfo reload() {
        Runtime runtime = Runtime.getRuntime();
        this.total = runtime.totalMemory();
        this.max = runtime.maxMemory();
        this.free = runtime.freeMemory();
        return this;
    }

    public static JvmUsageInfo load() {
        Runtime runtime = Runtime.getRuntime();
        long total = runtime.totalMemory();
        long max = runtime.maxMemory();
        long free = runtime.freeMemory();
        return new JvmUsageInfo(total, max, free);
    }
}
