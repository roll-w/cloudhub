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

package org.cloudhub.file.fs.meta;

import org.cloudhub.file.fs.container.ContainerFinder;
import org.cloudhub.file.fs.container.ContainerNameMeta;
import org.cloudhub.file.fs.container.ContainerFinder;
import org.cloudhub.file.fs.container.ContainerNameMeta;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author RollW
 */
public class LocalContainerGroupMeta implements ContainerGroupMeta {
    private final SerializedContainerGroupMeta serializedContainerGroupMeta;
    private final List<ContainerLocator> containerLocators;

    protected LocalContainerGroupMeta(
            List<ContainerLocator> containerLocators,
            SerializedContainerGroupMeta serializedContainerGroupMeta) {
        this.serializedContainerGroupMeta = serializedContainerGroupMeta;
        this.containerLocators = containerLocators;
    }

    public LocalContainerGroupMeta(
            SerializedContainerGroupMeta serializedContainerGroupMeta) {
        this(converts(serializedContainerGroupMeta), serializedContainerGroupMeta);
    }

    public LocalContainerGroupMeta(List<ContainerLocator> containerLocators) {
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
            ContainerNameMeta containerNameMeta =
                    ContainerNameMeta.parse(serializedContainerMeta.getLocator());
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
