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

import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import org.cloudhub.file.fs.LocalFileServer;
import org.cloudhub.file.fs.LockException;
import org.cloudhub.file.fs.ServerFile;
import org.cloudhub.file.fs.container.Container;
import org.cloudhub.file.fs.container.ContainerChecker;
import org.cloudhub.file.fs.container.ContainerProperties;
import org.cloudhub.file.fs.container.ContainerReadOpener;
import org.cloudhub.file.fs.container.validate.ContainerStatues;
import org.cloudhub.file.fs.container.validate.ContainerValidator;
import org.cloudhub.file.fs.meta.*;
import org.cloudhub.file.io.HasherInputStream;
import org.cloudhub.file.io.SeekableInputStream;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author RollW
 */
@Service
@SuppressWarnings({"UnstableApiUsage"})
public class ContainerCheckService implements ContainerChecker {
    private final ContainerReadOpener containerReadOpener;
    private final LocalFileServer localFileServer;
    private final ContainerMetaFactory containerMetaFactory;
    private final ServerFile metaDir, dataDir;
    private final MetadataLoader metadataLoader;

    public ContainerCheckService(ContainerReadOpener containerReadOpener,
                                 LocalFileServer localFileServer,
                                 ContainerProperties containerProperties) {
        this.containerReadOpener = containerReadOpener;
        this.localFileServer = localFileServer;
        this.containerMetaFactory = new ContainerMetaFactory();
        this.metaDir = localFileServer.getServerFileProvider().openFile(
                containerProperties.getMetaPath());
        this.dataDir = localFileServer.getServerFileProvider().openFile(
                containerProperties.getContainerPath());
        this.metadataLoader = new ContainerMetadataLoader(
                containerMetaFactory,
                dataDir,
                localFileServer
        );
    }

    @Override
    public ContainerStatues checkContainer(String containerLocator)
            throws IOException, MetadataException {
        ServerFile groupMetaFile = localFileServer.getServerFileProvider()
                .openFile(metaDir, ContainerMetaKeys.toMetaFileName(containerLocator));
        ContainerGroupMeta containerGroupMeta = containerMetaFactory
                .loadContainerGroupMeta(groupMetaFile);

        ContainerValidator containerValidator = new ContainerValidator(
                dataDir, localFileServer,
                containerLocator,
                containerGroupMeta,
                metadataLoader
        );
        return containerValidator.validate(this);
    }

    @Override
    public String calculateChecksum(Container container) throws LockException, IOException {
        Hasher crc32 = Hashing.crc32().newHasher();
        SeekableInputStream seekableInputStream =
                containerReadOpener.openContainerRead(container);
        HasherInputStream hasherInputStream = new HasherInputStream(seekableInputStream);
        hasherInputStream.addHasher("CRC32", crc32);
        readFully(hasherInputStream);
        containerReadOpener.closeContainerRead(container, hasherInputStream);
        return hasherInputStream.getHash("CRC32").toString();
    }

    public String calculateChecksum(ServerFile serverFile) throws IOException {
        Hasher crc32 = Hashing.crc32().newHasher();
        InputStream inputStream = serverFile.openInput();
        HasherInputStream hasherInputStream = new HasherInputStream(inputStream);
        hasherInputStream.addHasher("CRC32", crc32);
        readFully(hasherInputStream);
        inputStream.close();
        return hasherInputStream.getHash("CRC32").toString();
    }

    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
    public static final int EOF = -1;

    private static void readFully(InputStream input) throws IOException {
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        while (EOF != input.read(buffer)) {
            // leave it empty
        }
    }
}
