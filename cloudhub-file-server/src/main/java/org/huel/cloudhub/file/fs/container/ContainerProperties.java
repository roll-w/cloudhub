package org.huel.cloudhub.file.fs.container;

import java.io.File;

/**
 * Container Properties.
 *
 * @author RollW
 */
public class ContainerProperties {
    public static final String CONTAINER_PATH = "data";
    public static final String META_PATH = "meta";

    private final String stagePath;

    private final String filePath;
    /**
     * block size, in kb
     */
    private int blockSize;

    /**
     * block count
     */
    private int blockCount;

    public ContainerProperties(String filePath, String stagePath,
                               int blockSize, int blockCount) {
        this.filePath = filePath;
        this.stagePath = stagePath;
        this.blockSize = blockSize;
        this.blockCount = blockCount;
    }

    public String getContainerPath() {
        return filePath + File.separator + CONTAINER_PATH;
    }

    public String getMetaPath() {
        return filePath + File.separator + META_PATH;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getStagePath() {
        return stagePath;
    }

    public int getBlockSize() {
        return blockSize;
    }

    public void setBlockSize(int blockSize) {
        this.blockSize = blockSize;
    }

    public int getBlockCount() {
        return blockCount;
    }

    public void setBlockCount(int blockCount) {
        this.blockCount = blockCount;
    }

    public int getBlockSizeInBytes() {
        return blockSize * 1024;
    }

    public long getContainerSizeBytes() {
        return (long) blockCount * getBlockSizeInBytes();
    }
}
