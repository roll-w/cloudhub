package org.huel.cloudhub.meta.server.service.file;

/**
 * @author RollW
 */
public interface FileDownloadCallback {
    void onDownloadComplete();

    void onDownloadError(FileDownloadingException e);
}
