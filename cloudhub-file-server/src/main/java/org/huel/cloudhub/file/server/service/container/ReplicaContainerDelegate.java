package org.huel.cloudhub.file.server.service.container;

import org.huel.cloudhub.file.fs.FileAllocator;
import org.huel.cloudhub.file.fs.LocalFileServer;
import org.huel.cloudhub.file.fs.ServerFile;
import org.huel.cloudhub.file.fs.block.BlockMetaInfo;
import org.huel.cloudhub.file.fs.container.*;
import org.huel.cloudhub.file.fs.container.replica.ReplicaContainerCreator;
import org.huel.cloudhub.file.fs.container.replica.ReplicaContainerLoader;
import org.huel.cloudhub.file.fs.container.replica.ReplicaContainerNameMeta;
import org.huel.cloudhub.file.fs.container.replica.ReplicaGroup;
import org.huel.cloudhub.file.fs.meta.*;
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
public class ReplicaContainerDelegate implements ReplicaContainerLoader, ReplicaContainerCreator {
    private final Map<String, ReplicaGroup> replicaContainerGroups =
            new ConcurrentHashMap<>();
    private final ContainerProperties containerProperties;
    private final LocalFileServer localFileServer;
    private final ServerFile containerDir;

    public ReplicaContainerDelegate(ContainerProperties containerProperties,
                                    LocalFileServer localFileServer) {
        this.containerProperties = containerProperties;
        this.localFileServer = localFileServer;
        this.containerDir =
                localFileServer.getServerFileProvider().openFile(containerProperties.getContainerPath());
    }

    @Override
    public void loadInReplicaContainers(SerializedContainerGroupMeta containerMeta) throws IOException {
        for (SerializedContainerMeta serializedContainerMeta : containerMeta.getMetaList()) {
            Container container = loadInContainer(serializedContainerMeta);
            updatesContainer(container);
        }
    }

    private void updatesContainer(Container container) {
        ReplicaGroup replicaGroup = replicaContainerGroups.get(container.getSource());
        if (replicaGroup == null) {
            replicaGroup = new ReplicaGroup(container.getSource());
            replicaContainerGroups.put(container.getSource(), replicaGroup);
        }
        replicaGroup.put(container);
    }

    private Container loadInContainer(SerializedContainerMeta serializedContainerMeta) throws IOException {
        final String locator = serializedContainerMeta.getLocator();
        ServerFile file = localFileServer.getServerFileProvider()
                .openFile(containerDir, locator);
        ServerFile metaFile = localFileServer.getServerFileProvider()
                .openFile(containerDir, locator + ContainerLocation.META_SUFFIX);
        SerializedContainerBlockMeta containerBlockMeta = MetaReadWriteHelper.readContainerBlockMeta(metaFile);
        ReplicaContainerNameMeta nameMeta = ReplicaContainerNameMeta.parse(locator);

        List<BlockMetaInfo> blockMetaInfos = new ArrayList<>();
        containerBlockMeta.getBlockMetasList().forEach(serializeBlockFileMeta ->
                blockMetaInfos.add(BlockMetaInfo.deserialize(serializeBlockFileMeta, nameMeta.getSerial())));

        ContainerIdentity identity = buildIdentityFrom(nameMeta, containerBlockMeta);
        ContainerLocation location =
                new ContainerLocation(file.getPath());

        return new Container(location, nameMeta.getSourceId(), containerBlockMeta.getUsedBlock(),
                identity, blockMetaInfos, serializedContainerMeta.getVersion(), true);
    }

    @Override
    public Container findOrCreateContainer(String id, String source, long serial, SerializedContainerBlockMeta serializedContainerBlockMeta) throws IOException {
        ReplicaGroup replicaGroup = replicaContainerGroups.get(source);
        Container container = replicaGroup.getContainer(id, serial);
        if (container != null) {
            return container;
        }
        container = createReplicaContainer(id, source, serial, serializedContainerBlockMeta);
        replicaGroup.put(container);
        return container;
    }

    private Container createReplicaContainer(String id,
                                             String source,
                                             long serial,
                                             SerializedContainerBlockMeta containerBlockMeta) throws IOException {
        final String containerId = ContainerIdentity.toContainerId(id);
        ReplicaContainerNameMeta containerNameMeta =
                new ReplicaContainerNameMeta(source, containerId, serial);
        ContainerLocation location = new ContainerLocation(
                toDataPath(containerNameMeta.getName()),
                ContainerLocation.REPLICA_META_SUFFIX);
        ContainerIdentity identity = buildIdentityFrom(containerNameMeta, containerBlockMeta);
        return new Container(location, source,
                containerBlockMeta.getUsedBlock(),
                identity, List.of(), 1, true);
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
                .openFile(containerDir, container.getResourceLocator());
        boolean containerExists = containerFile.exists();
        containerFile.createFile();
        ServerFile metaFile = localFileServer.getServerFileProvider()
                .openFile(containerDir, container.getResourceLocator() + ContainerLocation.REPLICA_META_SUFFIX);
        metaFile.createFile();
        if (!containerExists) {
            allocateContainerSize(container);
        }
        MetaReadWriteHelper.writeContainerBlockMeta(serializedContainerBlockMeta, metaFile);
    }

    private void allocateContainerSize(Container container) throws IOException {
        try (FileAllocator allocator = new FileAllocator(container.getLocation())) {
            allocator.allocateSize(container.getLimitBytes());
        }
    }
}
