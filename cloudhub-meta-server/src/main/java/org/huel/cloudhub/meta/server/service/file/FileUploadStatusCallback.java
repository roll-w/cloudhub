package org.huel.cloudhub.meta.server.service.file;

import org.huel.cloudhub.meta.fs.FileObjectUploadStatus;

/**
 * @author RollW
 */
public interface FileUploadStatusCallback {
    void onNextStatus(FileObjectUploadStatus status);
}
