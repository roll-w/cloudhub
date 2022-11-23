package org.huel.cloudhub.server;

import oshi.hardware.HWDiskStore;
import oshi.util.Util;

import java.io.File;

/**
 * @author RollW
 */
public class DiskUsageInfo {
    private final long total;
    private long free;
    private long read;
    private long write;

    private final File file;

    public DiskUsageInfo(long total, long free, long read, long write, File file) {
        this.total = total;
        this.free = free;
        this.read = read;
        this.write = write;
        this.file = file;
    }

    public long getRead() {
        return read;
    }

    public long getWrite() {
        return write;
    }

    public long getTotal() {
        return total;
    }

    public long getFree() {
        return free;
    }

    public DiskUsageInfo fork() {
        return new DiskUsageInfo(total, free, read, write, file);
    }

    protected DiskUsageInfo reload(HWDiskStore store, long ms) {
        if (store == null || file == null) {
            return this;
        }
        final long prevReads = store.getReadBytes();
        final long prevWrites = store.getWriteBytes();
        long prevTms = store.getTimeStamp();
        Util.sleep(ms);
        store.updateAttributes();
        final long nextReads = store.getReadBytes();
        final long nextWrites = store.getWriteBytes();
        long nextTms = store.getTimeStamp();
        long diffTms = nextTms - prevTms;
        this.read = ServerHostInfo.calcRate(prevReads, nextReads, diffTms);
        this.write = ServerHostInfo.calcRate(prevWrites, nextWrites, diffTms);
        this.free = file.getUsableSpace();
        return this;
    }

    public static DiskUsageInfo load(String path) {
        if (path == null) {
            return new DiskUsageInfo(0, 0, 0, 0, null);
        }

        File file = new File(path);
        return new DiskUsageInfo(file.getTotalSpace(), 0, 0, 0, file);
    }

}
