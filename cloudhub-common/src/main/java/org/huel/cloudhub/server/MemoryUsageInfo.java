package org.huel.cloudhub.server;

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

    public MemoryUsageInfo reload(GlobalMemory memory) {
        this.total = memory.getTotal();
        this.free = memory.getAvailable();
        this.used = memory.getTotal() - memory.getAvailable();
        return this;
    }


    public static MemoryUsageInfo load(GlobalMemory memory) {
        return new MemoryUsageInfo().reload(memory);
    }
}
