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

package org.cloudhub.file.server.service.id;

import com.google.common.io.Files;
import org.cloudhub.file.CleanProperties;
import org.cloudhub.file.fs.container.ContainerProperties;
import org.cloudhub.server.ServerIdentifiable;
import org.cloudhub.server.ServerInitializeException;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.UUID;

/**
 * @author RollW
 */
@Service
public class FileServerIdService implements ServerIdentifiable {
    private final UUID uuid;
    private final String uuidString;

    private final ContainerProperties containerProperties;
    private final Properties properties = new CleanProperties();

    public static final String FILE_NAME = "ID_VERSION";

    private static final String COMMENT = "Cloudhub File Server ID, DO NOT MODIFY IT!";

    public FileServerIdService(ContainerProperties containerProperties)
            throws IOException, ServerInitializeException {
        this.containerProperties = containerProperties;
        uuid = initialId();
        uuidString = uuid.toString();
    }

    private UUID initialId() throws IOException, ServerInitializeException {
        File file = new File(containerProperties.getFilePath(), FILE_NAME);
        if (!file.exists()) {
            file.createNewFile();
            UUID uid = UUID.randomUUID();
            properties.setProperty(FILE_ID_KEY, uid.toString());
            persistProperties();
            return uid;
        }
        try (Reader reader = Files.asCharSource(file, StandardCharsets.UTF_8)
                .openBufferedStream()) {
            properties.load(reader);
        }
        if (properties.containsKey(META_ID_KEY)) {
            throw new ServerInitializeException(
                    "meta-server id found in ID_VERSION, it is incompatible with the file-server," +
                            " set another path for file server data. Path: " +
                            containerProperties.getFilePath() + "/" + FILE_NAME + ", mid: " +
                            properties.getProperty(META_ID_KEY)
            );
        }
        String id = properties.getProperty(FILE_ID_KEY);
        return UUID.fromString(id);
    }

    private void persistProperties() throws IOException {
        File file = new File(containerProperties.getFilePath(), FILE_NAME);
        try (Writer writer = Files.asCharSink(file, StandardCharsets.UTF_8)
                .openBufferedStream()) {
            properties.store(writer, COMMENT);
        }
    }

    @Override
    public String getServerId() {
        return uuidString;
    }

    public UUID getServerUuid() {
        return uuid;
    }
}
