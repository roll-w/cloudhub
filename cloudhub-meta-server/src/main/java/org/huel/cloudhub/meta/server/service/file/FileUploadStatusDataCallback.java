package org.huel.cloudhub.meta.server.service.file;

import org.huel.cloudhub.meta.fs.FileObjectUploadStatus;

/**
 * @author RollW
 */
public interface FileUploadStatusDataCallback extends FileUploadStatusCallback {
    @Override
    void onNextStatus(FileObjectUploadStatus status);

    void onCalc(String fileId);
}
