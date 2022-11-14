package org.huel.cloudhub.file.fs.meta;

import org.huel.cloudhub.file.fs.ServerFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author RollW
 */
public final class MetaReadWriteHelper {
    public static SerializedContainerBlockMeta readContainerBlockMeta(ServerFile serverFile) throws IOException {
        try (InputStream inputStream = serverFile.openInput()) {
            return SerializedContainerBlockMeta.parseFrom(inputStream);
        }
    }

    public static SerializedContainerGroupMeta readContainerMeta(ServerFile serverFile) throws IOException {
        try (InputStream inputStream = serverFile.openInput()) {
            return SerializedContainerGroupMeta.parseFrom(inputStream);
        }
    }

    public static void writeContainerGroupMeta(SerializedContainerGroupMeta containerGroupMeta,
                                               ServerFile file) throws IOException {
        try (OutputStream outputStream = file.openOutput(true)) {
            containerGroupMeta.writeTo(outputStream);
        }
    }


    public static void writeContainerBlockMeta(SerializedContainerBlockMeta containerBlockMeta, ServerFile file) throws IOException {
        try (OutputStream outputStream = file.openOutput(true)) {
            containerBlockMeta.writeTo(outputStream);
        }
    }

    private MetaReadWriteHelper() {
    }
}
