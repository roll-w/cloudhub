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
    TEMPORARY,
    /**
     * 处于文件服务器存储的过程中。文件不可用。
     */
    STORING,
    /**
     * 处于文件服务器存储中，副本同步状态。文件当前可用，副本不可用。
     */
    SYNCING,
    /**
     * 文件和副本现在都可用。
     */
    AVAILABLE,
    /**
     * 文件和副本都丢失。
     */
    LOST;

}
