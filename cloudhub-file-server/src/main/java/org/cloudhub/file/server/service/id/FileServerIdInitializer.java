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

import com.google.common.base.Strings;
import com.google.common.io.Files;
import org.cloudhub.util.CleanProperties;
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
public class FileServerIdInitializer implements ServerIdentifiable {
    public static final String FILE_NAME = "ID_VERSION";
    private static final String COMMENT = "Cloudhub File Server ID, DO NOT MODIFY IT!";

    private final ContainerProperties containerProperties;
    private final Properties properties = new CleanProperties();

    private final UUID uuid;
    private final String uuidString;

    public FileServerIdInitializer(ContainerProperties containerProperties)
            throws IOException, ServerInitializeException {
        this.containerProperties = containerProperties;
        uuid = initialId();
        uuidString = uuid.toString();
    }

    private UUID initialId() throws IOException, ServerInitializeException {
        File file = new File(containerProperties.getFilePath(), FILE_NAME);
        if (!file.exists()) {
            file.createNewFile();
            return generateIdAndPersist(file);
        }
        try (Reader reader = Files.asCharSource(file, StandardCharsets.UTF_8)
                .openBufferedStream()) {
            properties.load(reader);
        }
        if (properties.containsKey(META_ID_KEY)) {
            throw new ServerInitializeException(
                    "meta-server id found in ID_VERSION, it is incompatible with the file-server," +
                            " set another path for file-server data. Path: " +
                            containerProperties.getFilePath() + "/" + FILE_NAME + ", mid: " +
                            properties.getProperty(META_ID_KEY)
            );
        }
        String id = properties.getProperty(FILE_ID_KEY);
        if (Strings.isNullOrEmpty(id)) {
            return generateIdAndPersist(file);
        }
        try {
            return UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new ServerInitializeException(
                    "Invalid file-server ID found in ID_VERSION, set another path for file-server data, or delete the file. " +
                            "Path: " + containerProperties.getFilePath() + "/" + FILE_NAME + ", id: " + id
            );
        }
    }

    private UUID generateIdAndPersist(File file) throws IOException {
        if (properties.containsKey(META_ID_KEY)) {
            throw new IllegalStateException("meta-server id already exists in ID_VERSION");
        }

        UUID uid = UUID.randomUUID();
        properties.setProperty(META_ID_KEY, uid.toString());
        try (Writer writer = Files.asCharSink(file, StandardCharsets.UTF_8)
                .openBufferedStream()) {
            properties.store(writer, COMMENT);
        }
        return uid;
    }

    @Override
    public String getServerId() {
        return uuidString;
    }

    public UUID getServerUuid() {
        return uuid;
    }
}
