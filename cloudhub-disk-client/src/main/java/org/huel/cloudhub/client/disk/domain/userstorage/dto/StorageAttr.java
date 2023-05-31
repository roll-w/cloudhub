package org.huel.cloudhub.client.disk.domain.userstorage.dto;

import org.huel.cloudhub.client.disk.domain.operatelog.Operator;
import org.huel.cloudhub.client.disk.domain.userstorage.FileType;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author RollW
 */
public record StorageAttr(
        String fileName,
        byte[] content,
        String suffix,
        FileType parsedFileType,
        String fileId,
        long size,
        Operator operator
) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StorageAttr that)) return false;
        return Objects.equals(fileName, that.fileName) && Arrays.equals(content, that.content) && Objects.equals(suffix, that.suffix) && parsedFileType == that.parsedFileType && Objects.equals(fileId, that.fileId);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(fileName, suffix, parsedFileType, fileId);
        result = 31 * result + Arrays.hashCode(content);
        return result;
    }

    @Override
    public String toString() {
        return "StorageAttr{" +
                "fileName='" + fileName + '\'' +
                ", content=" + Arrays.toString(content) +
                ", suffix='" + suffix + '\'' +
                ", parsedFileType=" + parsedFileType +
                ", fileId='" + fileId + '\'' +
                '}';
    }


}
