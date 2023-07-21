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

package org.cloudhub.file.fs.container.validate;

import org.cloudhub.file.fs.FileServer;
import org.cloudhub.file.fs.ServerFile;
import org.cloudhub.file.fs.container.ChecksumCalculator;
import org.cloudhub.file.fs.meta.ContainerGroupMeta;
import org.cloudhub.file.fs.meta.ContainerMeta;
import org.cloudhub.file.fs.meta.MetadataException;
import org.cloudhub.file.fs.meta.MetadataLoader;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Validate a container with its metadata.
 *
 * @author RollW
 */
public class ContainerValidator {
    private final ServerFile dataDirectory;
    private final FileServer fileServer;
    private final String locator;
    private final ContainerGroupMeta containerGroupMeta;
    private final MetadataLoader metadataLoader;

    public ContainerValidator(ServerFile dataDirectory,
                              FileServer fileServer,
                              String locator,
                              ContainerGroupMeta containerGroupMeta,
                              MetadataLoader metadataLoader) {
        this.dataDirectory = dataDirectory;
        this.fileServer = fileServer;
        this.locator = locator;
        this.containerGroupMeta = containerGroupMeta;
        this.metadataLoader = metadataLoader;
    }

    public ContainerStatues validate(ChecksumCalculator checksumCalculator)
            throws IOException {
        Set<ContainerStatusCode> containerStatusCodes = check(checksumCalculator);
        boolean valid = containerStatusCodes.contains(ContainerStatusCode.VALID);
        return new ContainerStatues(containerStatusCodes, valid);
    }

    private Set<ContainerStatusCode> check(ChecksumCalculator checksumCalculator) throws IOException {
        Set<ContainerStatusCode> containerStatusCodes = new HashSet<>();
        ServerFile containerFile = fileServer.getServerFileProvider()
                .openFile(dataDirectory, locator);
        ContainerMeta containerMeta = null;
        try {
            containerMeta = tryLoadContainerMeta();
            if (containerMeta == null) {
                containerStatusCodes.add(ContainerStatusCode.META_LOST);
            }
        } catch (MetadataException e) {
            e.printStackTrace();
            containerStatusCodes.add(ContainerStatusCode.META_FAILED);
        }

        boolean containerExists  = containerFile.exists();
        if (!containerExists) {
            containerStatusCodes.add(ContainerStatusCode.CONTAINER_LOST);
        }
        if (containerMeta == null || !containerExists) {
            // unable to check checksum
            return containerStatusCodes;
        }
        String containerChecksum = containerMeta.getChecksum();
        String calcedChecksum = checksumCalculator.calculateChecksum(containerFile);

        if (!containerChecksum.equals(calcedChecksum)) {
            containerStatusCodes.add(ContainerStatusCode.CONTAINER_LOST);
        }
        if (containerStatusCodes.isEmpty()) {
            containerStatusCodes.add(ContainerStatusCode.VALID);
        }
        return containerStatusCodes;
    }

    private ContainerMeta tryLoadContainerMeta() throws IOException, MetadataException {
        ServerFile metaFile = fileServer.getServerFileProvider()
                .openFile(dataDirectory, locator + ".meta");
        if (!metaFile.exists()) {
            return null;
        }
        return containerGroupMeta.loadChildContainerMeta(
                metadataLoader, locator);
    }
}
