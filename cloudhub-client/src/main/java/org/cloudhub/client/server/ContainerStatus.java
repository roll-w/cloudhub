/*
 * Cloudhub - A high available, scalable distributed file system.
 * Copyright (C) 2022 Cloudhub
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.cloudhub.client.server;

import org.cloudhub.file.rpc.container.SerializedContainerInfo;

/**
 * Container Status with infos.
 *
 * @author RollW
 */
@SuppressWarnings({"unused"})
public class ContainerStatus {
    private String locator;// 容器定位符
    private String containerId;// 容器ID
    private String source;// 容器来源 Local(本地)，或者是远程的副本
    private long serial;// 序列号
    private int usedBlocks;// 已使用块数
    private int blockSize;// 块的大小。单位字节
    private int limitMbs;// 容器的大小。单位Mb
    private int limitBlocks;// 容器的块数上限

    public ContainerStatus(String locator, String containerId,
                           String source, long serial,
                           int usedBlocks, int blockSize,
                           int limitMbs, int limitBlocks) {
        this.locator = locator;
        this.containerId = containerId;
        this.source = source;
        this.serial = serial;
        this.usedBlocks = usedBlocks;
        this.blockSize = blockSize;
        this.limitMbs = limitMbs;
        this.limitBlocks = limitBlocks;
    }

    public String getLocator() {
        return locator;
    }

    public void setLocator(String locator) {
        this.locator = locator;
    }

    public String getContainerId() {
        return containerId;
    }

    public void setContainerId(String containerId) {
        this.containerId = containerId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public long getSerial() {
        return serial;
    }

    public void setSerial(long serial) {
        this.serial = serial;
    }

    public int getUsedBlocks() {
        return usedBlocks;
    }

    public void setUsedBlocks(int usedBlocks) {
        this.usedBlocks = usedBlocks;
    }

    public int getBlockSize() {
        return blockSize;
    }

    public void setBlockSize(int blockSize) {
        this.blockSize = blockSize;
    }

    public int getLimitMbs() {
        return limitMbs;
    }

    public void setLimitMbs(int limitMbs) {
        this.limitMbs = limitMbs;
    }

    public int getLimitBlocks() {
        return limitBlocks;
    }

    public void setLimitBlocks(int limitBlocks) {
        this.limitBlocks = limitBlocks;
    }

    public static ContainerStatus deserialize(SerializedContainerInfo containerInfo) {
        return new ContainerStatus(containerInfo.getLocator(),
                containerInfo.getContainerId(),
                containerInfo.getSource(),
                containerInfo.getSerial(),
                containerInfo.getUsedBlocks(),
                containerInfo.getBlockSize(),
                containerInfo.getLimitMbs(),
                containerInfo.getLimitBlocks());
    }
}
