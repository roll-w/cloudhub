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

package org.cloudhub.file.server.service.container;

import org.cloudhub.file.fs.FileAllocator;
import org.cloudhub.file.fs.LocalFileServer;
import org.cloudhub.file.fs.ServerFile;
import org.cloudhub.file.fs.block.BlockMetaInfo;
import org.cloudhub.file.fs.container.*;
import org.cloudhub.file.fs.container.replica.*;
import org.cloudhub.file.fs.meta.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author RollW
 */
@Service
public class ReplicaContainerDelegate implements ReplicaContainerLoader,
        ReplicaContainerCreator, ReplicaContainerFinder, ReplicaContainerDeleter {
    private final Map<String, SourcedContainerGroup> replicaContainerGroups =
            new ConcurrentHashMap<>();
    private final ContainerProperties containerProperties;
    private final LocalFileServer localFileServer;
    private final ServerFile containerDir;
    private final Logger logger = LoggerFactory.getLogger(ReplicaContainerDelegate.class);

    public ReplicaContainerDelegate(ContainerProperties containerProperties,
                                    LocalFileServer localFileServer) {
        this.containerProperties = containerProperties;
        this.localFileServer = localFileServer;
        this.containerDir =
                localFileServer.getServerFileProvider().openFile(containerProperties.getContainerPath());
    }

    @Override
    public void loadInReplicaContainers(SerializedReplicaContainerGroupMeta containerMeta) throws IOException {
        for (SerializedReplicaContainerMeta serializedContainerMeta : containerMeta.getMetaList()) {
            Container container = loadInContainer(serializedContainerMeta);
            updatesContainer(container);
        }
    }

    private void updatesContainer(Container container) {
        SourcedContainerGroup sourcedContainerGroup = replicaContainerGroups.get(container.getSource());
        if (sourcedContainerGroup == null) {
            sourcedContainerGroup = new SourcedContainerGroup(container.getSource());
            replicaContainerGroups.put(container.getSource(), sourcedContainerGroup);
        }
        sourcedContainerGroup.put(container);
    }

    private Container loadInContainer(SerializedReplicaContainerMeta serializedContainerMeta) throws IOException {
        final String locator = serializedContainerMeta.getLocator();
        ServerFile file = localFileServer.getServerFileProvider()
                .openFile(containerDir, locator);
        ServerFile metaFile = localFileServer.getServerFileProvider()
                .openFile(containerDir, locator + ContainerLocation.REPLICA_META_SUFFIX);
        SerializedContainerBlockMeta containerBlockMeta = MetaReadWriteHelper.readContainerBlockMeta(metaFile);
        ReplicaContainerNameMeta nameMeta = ReplicaContainerNameMeta.parse(locator);

        List<BlockMetaInfo> blockMetaInfos = new ArrayList<>();
        containerBlockMeta.getBlockMetasList().forEach(serializeBlockFileMeta ->
                blockMetaInfos.add(BlockMetaInfo.deserialize(serializeBlockFileMeta, nameMeta.getSerial())));

        ContainerIdentity identity = buildIdentityFrom(nameMeta, containerBlockMeta);
        ContainerLocation location =
                new ContainerLocation(file.getPath());

        return new Container(
                location, nameMeta.getSourceId(),
                containerBlockMeta.getUsedBlock(),
                identity, blockMetaInfos,
                serializedContainerMeta.getVersion(),
                true
        );
    }

    @Override
    public Container findOrCreateContainer(String id, String source,
                                           long serial,
                                           SerializedContainerBlockMeta serializedContainerBlockMeta) {
        final SourcedContainerGroup sourcedContainerGroup = replicaContainerGroups
                .computeIfAbsent(source, SourcedContainerGroup::new);
        Container container = sourcedContainerGroup.getContainer(id, serial);
        if (container != null) {
            return container;
        }
        container = createReplicaContainer(id, source, serial, serializedContainerBlockMeta);
        sourcedContainerGroup.put(container);
        return container;
    }

    private Container createReplicaContainer(String id,
                                             String source,
                                             long serial,
                                             SerializedContainerBlockMeta containerBlockMeta) {
        final String containerId = ContainerIdentity.toContainerId(id);
        ReplicaContainerNameMeta containerNameMeta =
                new ReplicaContainerNameMeta(source, containerId, serial);
        ContainerLocation location = new ContainerLocation(
                toDataPath(containerNameMeta.getName()),
                ContainerLocation.REPLICA_META_SUFFIX);
        ContainerIdentity identity = buildIdentityFrom(containerNameMeta, containerBlockMeta);
        List<BlockMetaInfo> blockMetaInfos = new ArrayList<>();
        for (SerializedBlockFileMeta serializedBlockFileMeta : containerBlockMeta.getBlockMetasList()) {
            BlockMetaInfo blockMetaInfo =
                    BlockMetaInfo.deserialize(serializedBlockFileMeta, identity.serial());
            blockMetaInfos.add(blockMetaInfo);
        }
        return new Container(location, source,
                containerBlockMeta.getUsedBlock(),
                identity, blockMetaInfos, 1, false);
    }

    private ContainerIdentity buildIdentityFrom(ReplicaContainerNameMeta nameMeta,
                                                SerializedContainerBlockMeta containerBlockMeta) {
        return new ContainerIdentity(
                nameMeta.getId(), containerBlockMeta.getCrc(),
                nameMeta.getSerial(), containerBlockMeta.getBlockCap(),
                containerBlockMeta.getBlockSize()
        );
    }

    private String toDataPath(String name) {
        return containerProperties.getContainerPath() + File.separator + name;
    }

    @Override
    public void createContainerWithMeta(Container container,
                                        SerializedContainerBlockMeta serializedContainerBlockMeta) throws IOException {
        containerDir.mkdirs();
        ServerFile containerFile = localFileServer.getServerFileProvider()
                .openFile(containerDir, container.getLocator());
        boolean containerExists = container.isUsable();
        containerFile.createFile();
        ServerFile metaFile = localFileServer.getServerFileProvider()
                .openFile(containerDir, container.getLocator() + ContainerLocation.REPLICA_META_SUFFIX);
        metaFile.createFile();
        if (!containerExists) {
            // first time create.
            allocateContainerSize(container);
            container.setUsable();
            updateContainerGroupMeta(container);
        }
        MetaReadWriteHelper.writeContainerBlockMeta(serializedContainerBlockMeta, metaFile);
    }

    private void allocateContainerSize(Container container) throws IOException {
        try (FileAllocator allocator = new FileAllocator(container.getLocation())) {
            allocator.allocateSize(container.getLimitBytes());
        }
    }

    public List<Container> listContainers() {
        return replicaContainerGroups
                .values().stream()
                .flatMap(sourcedContainerGroup ->
                        sourcedContainerGroup.listGroups().stream())
                .flatMap(containerGroup ->
                        containerGroup.containers().stream())
                .toList();
    }

    private void updateContainerGroupMeta(Container container) throws IOException {
        if (!container.isUsable()) {
            return;
        }
        SerializedReplicaContainerMeta meta = SerializedReplicaContainerMeta.newBuilder()
                .setLocator(container.getLocator())
                .setVersion(container.getVersion())
                .build();
        writeContainerMeta(meta);
    }

    @Async
    void writeContainerMeta(SerializedReplicaContainerMeta containerMeta) throws IOException {
        ReplicaContainerNameMeta fileNameMeta =
                ReplicaContainerNameMeta.parse(containerMeta.getLocator());
        String fileName = fileNameMeta.getSourceId() + "_" +
                ContainerIdentity.toCmetaId(fileNameMeta.getId());

        ServerFile file = localFileServer.getServerFileProvider().openFile(
                containerProperties.getMetaPath(),
                fileName + ContainerMetaKeys.REPLICA_CONTAINER_META_SUFFIX);
        file.createFile();
        SerializedReplicaContainerGroupMeta containerGroupMeta = MetaReadWriteHelper.readReplicaContainerMeta(file);

        List<SerializedReplicaContainerMeta> containerMetas = new ArrayList<>(containerGroupMeta.getMetaList());
        containerMetas.removeIf(serializedContainerMeta ->
                serializedContainerMeta.getLocator().equals(containerMeta.getLocator()));
        containerMetas.add(containerMeta);

        containerGroupMeta = SerializedReplicaContainerGroupMeta.newBuilder()
                .addAllMeta(containerMetas)
                .build();

        MetaReadWriteHelper.writeReplicaContainerGroupMeta(containerGroupMeta, file);
    }

    @Override
    public ContainerGroup findContainerGroup(String containerId, String source) {
        SourcedContainerGroup group = replicaContainerGroups.get(source);
        if (group == null) {
            return null;
        }
        return group.getGroup(containerId);
    }

    @Override
    public void deleteReplicaContainer(String id, long serial, String source) throws IOException {
        ContainerGroup group = findContainerGroup(id, source);
        if (group == null) {
            return;
        }
        Container container = group.getContainer(serial);
        if (container == null) {
            return;
        }
        group.remove(container);
        removeContainer(container);
    }

    private void removeContainer(Container container) throws IOException {
        ServerFile file = localFileServer.getServerFileProvider().openFile(containerDir,
                container.getLocator());
        ServerFile metaFile = localFileServer.getServerFileProvider().openFile(containerDir,
                container.getLocator() + ContainerLocation.REPLICA_META_SUFFIX);
        removeContainerGroupMeta(container);
        file.delete();
        metaFile.delete();
    }

    private void removeContainerGroupMeta(Container container) throws IOException {
        if (!container.isUsable()) {
            return;
        }
        removeContainerMeta(container.getSource(),
                container.getIdentity().id(), container.getSerial());
    }

    @Async
    void removeContainerMeta(String source, String containerId, long serial) throws IOException {
        if (ContainerFinder.isLocal(source)) {
            return;
        }
        String fileName = source + "_" + ContainerIdentity.toCmetaId(containerId);
        ServerFile file = localFileServer.getServerFileProvider()
                .openFile(containerProperties.getMetaPath(),
                        fileName + ContainerMetaKeys.REPLICA_CONTAINER_META_SUFFIX);
        if (!file.exists()) {
            return;
        }
        SerializedReplicaContainerGroupMeta containerGroupMeta = MetaReadWriteHelper.readReplicaContainerMeta(file);
        List<SerializedReplicaContainerMeta> containerMetas = new ArrayList<>(containerGroupMeta.getMetaList());
        String containerLocator = new ReplicaContainerNameMeta(source, containerId, serial).getName();
        containerMetas.removeIf(serializedContainerMeta ->
                serializedContainerMeta.getLocator().equals(containerLocator));
        containerGroupMeta = SerializedReplicaContainerGroupMeta.newBuilder()
                .addAllMeta(containerMetas)
                .build();
        MetaReadWriteHelper.writeReplicaContainerGroupMeta(containerGroupMeta, file);

        if (file.length() == 0) {
            file.delete();
        }
    }
}
