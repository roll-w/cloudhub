package org.huel.cloudhub.meta.fs;

/**
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
     * 处于文件服务器存储中。文件当前可用。
     */
    UPLOADED;
}
