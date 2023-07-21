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

package org.cloudhub.file.fs.container;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.cloudhub.file.fs.block.BlockMetaInfo;
import org.cloudhub.file.fs.block.FileBlockMetaInfo;
import org.cloudhub.file.fs.block.BlockMetaInfo;
import org.cloudhub.file.fs.block.FileBlockMetaInfo;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author RollW
 */
public class ContainerGroup {
    private final String sourceId;
    private final String containerId;
    private long blockSizeInBytes = -1;
    private volatile long latestSerial;

    private final Map<String, Container> containers = new ConcurrentHashMap<>();
    private final Map<Long, List<FreeBlockInfo>> serialFreeBlockInfos = new ConcurrentHashMap<>();
    private final Set<String> fileIds = ConcurrentHashMap.newKeySet();

    public ContainerGroup(String containerId, String sourceId) {
        this.containerId = containerId;
        this.sourceId = sourceId;
    }

    public ContainerGroup(String containerId, String sourceId, Collection<Container> containers) {
        this.containerId = containerId;
        this.sourceId = sourceId;
        containers.forEach(this::put);
    }

    public ContainerGroup(String containerId, String sourceId, Container container) {
        this.containerId = containerId;
        this.sourceId = sourceId;
        put(container);
    }

    @SuppressWarnings("all")
    public void put(Container container) {
        final long latestSerial = Math.max(container.getIdentity().serial(), this.latestSerial);
        this.latestSerial = latestSerial;
        containers.put(container.getLocator(), container);
        container.getBlockMetaInfos().forEach(blockMetaInfo ->
                fileIds.add(blockMetaInfo.getFileId()));
        List<FreeBlockInfo> containerFreeBlocks = serialFreeBlockInfos
                .computeIfAbsent(container.getIdentity().serial(),
                        v -> new ArrayList<>());
        containerFreeBlocks.addAll(container.getFreeBlockInfos());
        if (blockSizeInBytes > 0) {
            return;
        }
        // lazy load
        blockSizeInBytes = container.getIdentity().blockSizeBytes();
    }

    public void remove(Container container) {
        containers.remove(container.getLocator());
        container.getBlockMetaInfos().forEach(blockMetaInfo ->
                fileIds.remove(blockMetaInfo.getFileId()));
        // Delete the file id directly.
        // Because even if there were any files left,
        // it wouldn't be complete.
        serialFreeBlockInfos.remove(container.getIdentity().serial());
        if (container.getIdentity().serial() == latestSerial) {
            latestSerial = latestSerial - 1;
        }
    }

    public Collection<Container> containers() {
        return Collections.unmodifiableCollection(containers.values());
    }

    public boolean hasSerial(long serial) {
        String name = ContainerLocation.toContainerName(containerId, sourceId, serial);
        return containers.containsKey(name);
    }


    public boolean hasFile(String fileId) {
        return fileIds.contains(fileId);
    }

    public Collection<String> getFileIds() {
        return Collections.unmodifiableSet(fileIds);
    }

    public long lastSerial() {
        return latestSerial;
    }

    @Nullable
    public Container getContainer(long serial) {
        String name =
                ContainerLocation.toContainerName(containerId, sourceId, serial);
        return containers.get(name);
    }

    public Container latestContainer() {
        return getContainer(latestSerial);
    }

    public List<Container> writableContainers() {
        return containers.values().stream()
                .sorted(Comparator.comparingLong(Container::getSerial))
                .filter(container -> !container.isReachLimit())
                .toList();
    }

    @NonNull
    public List<Container> containersWithFile(String fileId) {
        return containers().stream()
                .filter(container -> container.hasFileId(fileId))
                .sorted(Comparator.comparingLong(container -> container.getIdentity().serial()))
                .toList();
    }

    public String getSourceId() {
        return sourceId;
    }

    public String getContainerId() {
        return containerId;
    }

    public long getBlockSizeInBytes() {
        return blockSizeInBytes;
    }

    public FileBlockMetaInfo getFileBlockMetaInfo(String fileId) {
        List<Container> fileContainers = containersWithFile(fileId);
        if (fileContainers.isEmpty()) {
            return null;
        }

        List<BlockMetaInfo> blockMetaInfos = new ArrayList<>();
        for (Container fileContainer : fileContainers) {
            BlockMetaInfo blockMetaInfo =
                    fileContainer.getBlockMetaInfoByFile(fileId);
            blockMetaInfos.add(blockMetaInfo);
        }

        BlockMetaInfo last = blockMetaInfos.get(blockMetaInfos.size() - 1);
        return new FileBlockMetaInfo(
                fileId, blockMetaInfos, blockSizeInBytes,
                last.getValidBytes());
    }


    public List<FileBlockMetaInfo> listFileBlockMetaInfos() {
        List<FileBlockMetaInfo> fileBlockMetaInfos = new ArrayList<>();
        for (String fileId : fileIds) {
            FileBlockMetaInfo fileBlockMetaInfo = getFileBlockMetaInfo(fileId);
            fileBlockMetaInfos.add(fileBlockMetaInfo);
        }
        return fileBlockMetaInfos;
    }
}
