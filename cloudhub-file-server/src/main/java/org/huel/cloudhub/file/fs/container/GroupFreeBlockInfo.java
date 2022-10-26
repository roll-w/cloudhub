package org.huel.cloudhub.file.fs.container;

/**
 * @author RollW
 */
public class GroupFreeBlockInfo {
    private final int serial;
    private final FreeBlockInfo freeBlockInfo;

    public GroupFreeBlockInfo(int serial, FreeBlockInfo freeBlockInfo) {
        this.serial = serial;
        this.freeBlockInfo = freeBlockInfo;
    }

    public GroupFreeBlockInfo(int serial, int start, int count) {
        this.serial = serial;
        this.freeBlockInfo = new FreeBlockInfo(start, count);
    }

    public int getSerial() {
        return serial;
    }

    public int getStart() {
        return freeBlockInfo.getStart();
    }

    public int getCount() {
        return freeBlockInfo.getCount();
    }
}
