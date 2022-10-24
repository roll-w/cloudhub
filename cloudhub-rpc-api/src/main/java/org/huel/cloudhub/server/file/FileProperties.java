package org.huel.cloudhub.server.file;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.File;

/**
 * @author RollW
 */
@ConfigurationProperties("cloudhub.file")
public class FileProperties {
    static final String CONTAINER_PATH = "data";
    static final String META_PATH = "meta";

    private String filePath = "dfs";
    /**
     * block size, in kb
     */
    private int blockSize = 64;

    /**
     * block count
     */
    private int blockCount = 1024;

    public FileProperties(String filePath, int blockSize, int blockCount) {
        this.filePath = filePath;
        this.blockSize = blockSize;
        this.blockCount = blockCount;
    }

    public FileProperties() {
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getContainerPath() {
        return filePath + File.separator + CONTAINER_PATH;
    }

    public String getMetaPath() {
        return filePath + File.separator + META_PATH;
    }

    public void setBlockSize(int blockSize) {
        this.blockSize = blockSize;
    }

    public int getBlockSize() {
        return blockSize;
    }

    public int getBlockCount() {
        return blockCount;
    }

    public void setBlockCount(int blockCount) {
        this.blockCount = blockCount;
    }
}
