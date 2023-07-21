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
import org.cloudhub.file.fs.container.ContainerProperties;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * @author RollW
 */
@Service
public class ServerIdService {
    private final UUID uuid;
    private final String uuidString;

    private final ContainerProperties containerProperties;

    public static final String ID_KEY = "fid";


    public ServerIdService(ContainerProperties containerProperties) throws IOException {
        this.containerProperties = containerProperties;
        uuid = initialId();
        uuidString = uuid.toString();
    }

    private UUID initialId() throws IOException {
        // TODO: move to properties

        File file = new File(containerProperties.getFilePath(), "ID_VERSION");
        if (!file.exists()) {
            file.createNewFile();
            UUID uid = UUID.randomUUID();
            Files.write(uid.toString().getBytes(StandardCharsets.UTF_8), file);
            return uid;
        }
        String firstLine = Files.asCharSource(file, StandardCharsets.UTF_8).readFirstLine();
        if (firstLine == null) {
            boolean delete = file.delete();
            if (!delete) {
                throw new IllegalStateException("failed delete ID_VERSION file.");
            }
            return initialId();
        }
        return UUID.fromString(firstLine);
    }

    public String getServerId() {
        return uuidString;
    }

    public UUID getServerUuid() {
        return uuid;
    }
}
