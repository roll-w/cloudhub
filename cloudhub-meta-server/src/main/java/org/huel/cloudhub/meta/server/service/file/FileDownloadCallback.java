package org.huel.cloudhub.meta.server.service.file;

import org.huel.cloudhub.file.rpc.block.DownloadBlockResponse;

/**
 * @author RollW
 */
public interface FileDownloadCallback {
    void onDownloadComplete();

    void onDownloadError(FileDownloadingException e);

    default void onSaveCheckMessage(DownloadBlockResponse.CheckMessage checkMessage) {
    }
}
