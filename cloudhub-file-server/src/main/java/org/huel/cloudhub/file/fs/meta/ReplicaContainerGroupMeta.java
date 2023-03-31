package org.huel.cloudhub.file.fs.meta;

import org.huel.cloudhub.file.fs.container.ContainerFinder;
import org.huel.cloudhub.file.fs.container.replica.ReplicaContainerNameMeta;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author RollW
 */
public class ReplicaContainerGroupMeta implements ContainerGroupMeta {
    private final SerializedReplicaContainerGroupMeta serializedContainerGroupMeta;
    private final List<ContainerLocator> containerLocators;

    protected ReplicaContainerGroupMeta(
            List<ContainerLocator> containerLocators,
            SerializedReplicaContainerGroupMeta serializedContainerGroupMeta) {
        this.serializedContainerGroupMeta = serializedContainerGroupMeta;
        this.containerLocators = containerLocators;
    }

    public ReplicaContainerGroupMeta(
            SerializedReplicaContainerGroupMeta serializedContainerGroupMeta) {
        this(converts(serializedContainerGroupMeta), serializedContainerGroupMeta);
    }

    public ReplicaContainerGroupMeta(List<ContainerLocator> containerLocators) {
        this(containerLocators, converts(containerLocators));
    }

    private static SerializedReplicaContainerGroupMeta converts(
            List<ContainerLocator> containerLocators) {
        List<SerializedReplicaContainerMeta> serializedContainerMetas = new ArrayList<>();
        for (ContainerLocator containerLocator : containerLocators) {
            SerializedReplicaContainerMeta serializedContainerMeta = SerializedReplicaContainerMeta.newBuilder()
                    .setVersion(containerLocator.getVersion())
                    .setLocator(containerLocator.getLocator())
                    .build();
            serializedContainerMetas.add(serializedContainerMeta);
        }
        return SerializedReplicaContainerGroupMeta.newBuilder()
                .addAllMeta(serializedContainerMetas)
                .build();
    }

    private static List<ContainerLocator> converts(
            SerializedReplicaContainerGroupMeta serializedReplicaContainerGroupMeta) {
        List<ContainerLocator> containerLocators = new ArrayList<>();
        for (SerializedReplicaContainerMeta serializedContainerMeta :
                serializedReplicaContainerGroupMeta.getMetaList()) {
            ReplicaContainerNameMeta containerNameMeta =
                    ReplicaContainerNameMeta.parse(serializedContainerMeta.getLocator());
            ContainerLocatorInfo containerLocatorInfo = new ContainerLocatorInfo(
                    serializedContainerMeta.getLocator(),
                    containerNameMeta.getId(),
                    containerNameMeta.getSerial(),
                    serializedContainerMeta.getVersion(),
                    ContainerFinder.LOCAL
            );
            containerLocators.add(containerLocatorInfo);
        }
        return containerLocators;
    }

    @Override
    public List<? extends ContainerLocator> getChildLocators() {
        return Collections.unmodifiableList(containerLocators);
    }

    @Override
    public ContainerLocator getChildLocator(String locator) {
        return null;
    }

    @Override
    public List<? extends ContainerMeta> loadChildContainerMeta(MetadataLoader loader)
            throws IOException, MetadataException {
        List<ContainerMeta> containerMetas = new ArrayList<>();
        for (ContainerLocator containerLocator : containerLocators) {
            ContainerMeta containerMeta = loader.loadContainerMeta(containerLocator);
            containerMetas.add(containerMeta);
        }
        return containerMetas;
    }

    @Override
    public ContainerMeta loadChildContainerMeta(MetadataLoader loader, String locator)
            throws IOException, MetadataException {
        ContainerLocator containerLocator = findContainerLocator(locator);
        if (containerLocator == null) {
            throw new MetadataException("Container locator not found: " + locator);
        }
        return loader.loadContainerMeta(containerLocator);
    }

    private ContainerLocator findContainerLocator(String locator) {
        for (ContainerLocator containerLocator : containerLocators) {
            if (containerLocator.getLocator().equals(locator)) {
                return containerLocator;
            }
        }
        return null;
    }

    @Override
    public String getSource() {
        return ContainerFinder.LOCAL;
    }

    @Override
    public void writeTo(OutputStream outputStream) throws IOException {
        serializedContainerGroupMeta.writeTo(outputStream);
    }
}
