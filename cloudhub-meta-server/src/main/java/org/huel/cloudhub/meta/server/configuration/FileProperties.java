package org.huel.cloudhub.meta.server.configuration;

/**
 * @author RollW
 */
public class FileProperties {
    private final String dataPath;

    private final String tempFilePath;
    /**
     * Upload block size, in kb
     */
    private final int blockSize;


    public FileProperties(String dataPath, String tempFilePath, int blockSize) {
        this.dataPath = dataPath;
        this.tempFilePath = tempFilePath;
        this.blockSize = blockSize;
    }

    public String getDataPath() {
        return dataPath;
    }

    public String getTempFilePath() {
        return tempFilePath;
    }

    public int getBlockSize() {
        return blockSize;
    }

    public int getBlockSizeInBytes() {
        return blockSize * 1024;
    }
}
