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

import org.cloudhub.file.fs.container.ContainerLocation;
import org.cloudhub.file.fs.meta.ContainerMeta;
import org.cloudhub.file.fs.meta.MetadataCacheable;
import org.cloudhub.file.fs.meta.MetadataLoader;
import org.cloudhub.file.fs.meta.cache.MetadataFullyCacheStrategy;
import org.cloudhub.file.fs.FileServer;
import org.cloudhub.file.fs.ServerFile;
import org.cloudhub.file.fs.container.ContainerLocation;
import org.cloudhub.file.fs.meta.ContainerLocator;
import org.cloudhub.file.fs.meta.ContainerMeta;
import org.cloudhub.file.fs.meta.ContainerMetaFactory;
import org.cloudhub.file.fs.meta.MetadataCacheStrategy;
import org.cloudhub.file.fs.meta.MetadataCacheable;
import org.cloudhub.file.fs.meta.MetadataException;
import org.cloudhub.file.fs.meta.MetadataLoader;
import org.cloudhub.file.fs.meta.cache.MetadataFullyCacheStrategy;

import java.io.IOException;

/**
 * @author RollW
 */
public class ContainerMetadataLoader implements MetadataLoader {
    private final ContainerMetaFactory containerMetaFactory;
    private MetadataCacheStrategy metadataCacheStrategy;
    private final ServerFile dataDirectory;
    private final FileServer fileServer;

    public ContainerMetadataLoader(ContainerMetaFactory containerMetaFactory,
                                   ServerFile dataDirectory,
                                   FileServer fileServer) {
        this.containerMetaFactory = containerMetaFactory;
        this.dataDirectory = dataDirectory;
        this.fileServer = fileServer;
        this.metadataCacheStrategy = new MetadataFullyCacheStrategy();
    }

    @Override
    public ContainerMeta loadContainerMeta(ContainerLocator containerLocator)
            throws IOException, MetadataException {
        ServerFile metaFile = fileServer.getServerFileProvider()
                .openFile(dataDirectory, containerLocator.getLocator() + ContainerLocation.META_SUFFIX);
        MetadataCacheable<ContainerMeta> containerMetaMetadataCacheable =
                metadataCacheStrategy.get(containerLocator.getLocator());
        if (containerMetaMetadataCacheable != null) {
            return containerMetaMetadataCacheable.getMeta();
        }
        ContainerMeta containerMeta =
                containerMetaFactory.loadContainerMeta(metaFile, containerLocator);
        metadataCacheStrategy.cache(containerMeta);

        return containerMeta;
    }

    @Override
    public void setMetadataCacheStrategy(MetadataCacheStrategy strategy) {
        this.metadataCacheStrategy = strategy;
    }
}
