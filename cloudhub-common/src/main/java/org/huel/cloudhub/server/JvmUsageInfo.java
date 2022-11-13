package org.huel.cloudhub.server;

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
        this.total = Runtime.getRuntime().totalMemory();
        this.max = Runtime.getRuntime().maxMemory();
        this.free = Runtime.getRuntime().freeMemory();
        return this;
    }

    public static JvmUsageInfo load() {
        long total = Runtime.getRuntime().totalMemory();
        long max = Runtime.getRuntime().maxMemory();
        long free = Runtime.getRuntime().freeMemory();
        return new JvmUsageInfo(total, max, free);
    }
}
