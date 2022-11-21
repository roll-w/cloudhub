package org.huel.cloudhub.meta.fs;

/**
 * 上传状态
 *
 * @author RollW
 */
public enum FileObjectUploadStatus {
    /**
     * 处于元文件服务器的暂存区。文件不可用。
     */
    TEMPORARY(true, false),
    /**
     * 处于文件服务器存储的过程中。文件不可用。
     */
    STORING(true, false),
    /**
     * 处于文件服务器存储中，副本同步状态。文件当前可用，副本不可用。
     */
    SYNCING(true, true),
    /**
     * 文件和副本现在都可用。
     */
    AVAILABLE(true, true),
    /**
     * 文件和副本都丢失。
     */
    LOST(false, true);

    private final boolean available;
    private final boolean lastStatus;

    FileObjectUploadStatus(boolean available, boolean lastStatus) {
        this.available = available;
        this.lastStatus = lastStatus;
    }

    public boolean isAvailable() {
        return available;
    }

    public boolean isLastStatus() {
        return lastStatus;
    }
}
