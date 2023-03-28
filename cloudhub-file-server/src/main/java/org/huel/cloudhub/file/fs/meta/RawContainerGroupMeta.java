package org.huel.cloudhub.file.fs.meta;

import org.huel.cloudhub.file.fs.container.ContainerFinder;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author RollW
 */
public class RawContainerGroupMeta implements ContainerGroupMeta {
    private final SerializedContainerGroupMeta serializedContainerGroupMeta;
    private final List<ContainerLocator> containerLocators;

    protected RawContainerGroupMeta(
            List<ContainerLocator> containerLocators,
            SerializedContainerGroupMeta serializedContainerGroupMeta) {
        this.serializedContainerGroupMeta = serializedContainerGroupMeta;
        this.containerLocators = containerLocators;
    }

    public RawContainerGroupMeta(
            SerializedContainerGroupMeta serializedContainerGroupMeta) {
        this(converts(serializedContainerGroupMeta), serializedContainerGroupMeta);
    }

    public RawContainerGroupMeta(List<ContainerLocator> containerLocators) {
        this(containerLocators, converts(containerLocators));
    }

    private static SerializedContainerGroupMeta converts(
            List<ContainerLocator> containerLocators) {
        List<SerializedContainerMeta> serializedContainerMetas = new ArrayList<>();
        for (ContainerLocator containerLocator : containerLocators) {
            SerializedContainerMeta serializedContainerMeta = SerializedContainerMeta.newBuilder()
                    .setVersion(containerLocator.getVersion())
                    .setLocator(containerLocator.getLocator())
                    .build();
            serializedContainerMetas.add(serializedContainerMeta);
        }
        return SerializedContainerGroupMeta.newBuilder()
                .addAllMeta(serializedContainerMetas)
                .build();
    }

    private static List<ContainerLocator> converts(
            SerializedContainerGroupMeta serializedContainerGroupMeta) {
        List<ContainerLocator> containerLocators = new ArrayList<>();
        for (SerializedContainerMeta serializedContainerMeta :
                serializedContainerGroupMeta.getMetaList()) {
            ContainerLocatorInfo containerLocatorInfo = new ContainerLocatorInfo(
                    ContainerFinder.LOCAL,
                    serializedContainerMeta.getVersion(),
                    serializedContainerMeta.getLocator()
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
