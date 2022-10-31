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

    private String tempFilePath = ".temp";

    private String stageFilePath = ".staging";
    /**
     * block size, in kb
     */
    private int blockSize = 64;

    /**
     * block count
     */
    private int blockCount = 1024;

    /**
     * in mb. max request size
     */
    private int maxRequestSize = 40;

    public FileProperties(String filePath, String tempFilePath,
                          String stageFilePath,
                          int blockSize, int blockCount,
                          int maxRequestSize) {
        this.filePath = filePath;
        this.tempFilePath = tempFilePath;
        this.stageFilePath = stageFilePath;
        this.blockSize = blockSize;
        this.blockCount = blockCount;
        this.maxRequestSize = maxRequestSize;
    }

    public FileProperties() {
    }

    public String getTempFilePath() {
        return tempFilePath;
    }

    public void setTempFilePath(String tempFilePath) {
        this.tempFilePath = tempFilePath;
    }

    public String getStageFilePath() {
        return stageFilePath;
    }

    public void setStageFilePath(String stageFilePath) {
        this.stageFilePath = stageFilePath;
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

    public int getBlockSizeInBytes() {
        return blockSize * 1024;
    }

    public int getBlockCount() {
        return blockCount;
    }

    public void setBlockCount(int blockCount) {
        this.blockCount = blockCount;
    }

    public int getMaxRequestSize() {
        return maxRequestSize;
    }

    public void setMaxRequestSize(int maxRequestSize) {
        this.maxRequestSize = maxRequestSize;
    }

    public long getMaxRequestSizeBytes() {
        return maxRequestSize * 1024L * 1024L;
    }

    public long getContainerSizeBytes() {
        return (long) blockCount * getBlockSizeInBytes();
    }


}
