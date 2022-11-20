package org.huel.cloudhub.client.data.dto.fs;

/**
 * Container Status with infos.
 *
 * @author RollW
 */
public class ContainerStatus {
    private String locator;// 容器定位符
    private String containerId;// 容器ID
    private String source;// 容器来源 Local(本地)，或者是远程的副本
    private long serial;// 序列号

    private int usedBlocks;// 已使用块数
    private int blockSize;// 块的大小。单位字节
    private int limitMbs;// 容器的大小。单位Mb
    private int limitBlocks;// 容器的块数上限

}
