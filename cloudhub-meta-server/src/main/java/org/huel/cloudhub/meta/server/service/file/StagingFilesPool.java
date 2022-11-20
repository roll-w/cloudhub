package org.huel.cloudhub.meta.server.service.file;

import org.huel.cloudhub.meta.fs.FileObjectUploadStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * @author RollW
 */
public class StagingFilesPool {
    private final Map<String, FileStatus> fileStatusMap = new HashMap<>();

    private static class FileStatus {
        private final String fileId;
        private FileObjectUploadStatus status;

        private FileStatus(String fileId, FileObjectUploadStatus status) {
            this.fileId = fileId;
            this.status = status;
        }

        public String getFileId() {
            return fileId;
        }

        public void setStatus(FileObjectUploadStatus status) {
            this.status = status;
        }

        public FileObjectUploadStatus getStatus() {
            return status;
        }
    }
}
