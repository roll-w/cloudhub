package org.huel.cloudhub.file.server.service.file;

/**
 * @author RollW
 */
public class FileUtils {
    public static String getExtensionName(String fileName) {
        if (fileName == null) {
            throw new NullPointerException("fileName cannot be null.");
        }
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
    }

    private FileUtils() {
    }
}
