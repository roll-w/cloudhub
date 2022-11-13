package org.huel.cloudhub.server;

import oshi.hardware.HWDiskStore;

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

    public DiskUsageInfo reload(HWDiskStore store, long ms) {
        final long prevReads = store.getReadBytes();
        final long prevWrites = store.getWriteBytes();
        store.updateAttributes();
        final long nextReads = store.getReadBytes();
        final long nextWrites = store.getWriteBytes();
        this.read = ServerHostInfo.calcRate(prevReads, nextReads, ms);
        this.write = ServerHostInfo.calcRate(prevWrites, nextWrites, ms);
        this.free = file.getUsableSpace();
        return this;
    }

    public static DiskUsageInfo load(String path) {
        File file =  new File(path);
        return new DiskUsageInfo(file.getTotalSpace(), 0, 0, 0,file);
    }

}
