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

package org.cloudhub.meta.server.service.id;

import com.google.common.io.Files;
import org.cloudhub.file.CleanProperties;
import org.cloudhub.meta.server.configuration.FileProperties;
import org.cloudhub.server.ServerIdentifiable;
import org.cloudhub.server.ServerInitializeException;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.UUID;

/**
 * @author RollW
 */
@Service
public class MetaServerIdInitializer implements ServerIdentifiable {
    public static final String FILE_NAME = "ID_VERSION";
    private static final String COMMENT = "Cloudhub Meta Server ID, DO NOT MODIFY IT!";

    private final FileProperties fileProperties;
    private final Properties properties = new CleanProperties();

    private final UUID uuid;
    private final String uuidString;

    public MetaServerIdInitializer(FileProperties fileProperties)
            throws IOException, ServerInitializeException {
        this.fileProperties = fileProperties;
        uuid = initialId();
        uuidString = uuid.toString();
    }

    private UUID initialId() throws IOException, ServerInitializeException {
        File file = new File(fileProperties.getDataPath(), FILE_NAME);
        if (!file.exists()) {
            file.createNewFile();
            UUID uid = UUID.randomUUID();
            properties.setProperty(META_ID_KEY, uid.toString());
            persistProperties();
            return uid;
        }
        try (Reader reader = Files.asCharSource(file, StandardCharsets.UTF_8)
                .openBufferedStream()) {
            properties.load(reader);
        }
        if (properties.containsKey(FILE_ID_KEY)) {
            throw new ServerInitializeException(
                    "file-server id found in ID_VERSION, it is incompatible with the meta-server," +
                            " set another path for meta-server data. Path: " +
                            fileProperties.getDataPath() + "/" + FILE_NAME + ", fid: " +
                            properties.getProperty(FILE_ID_KEY)
            );
        }
        String id = properties.getProperty(META_ID_KEY);
        return UUID.fromString(id);
    }

    @Override
    public String getServerId() {
        return uuidString;
    }

    public UUID getUuid() {
        return uuid;
    }

    private void persistProperties() throws IOException {
        File file = new File(fileProperties.getDataPath(), FILE_NAME);
        try (Writer writer = Files.asCharSink(file, StandardCharsets.UTF_8)
                .openBufferedStream()) {
            properties.store(writer, COMMENT);
        }
    }
}
