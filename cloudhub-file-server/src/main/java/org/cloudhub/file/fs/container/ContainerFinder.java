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

package org.cloudhub.file.fs.container;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;

/**
 * @author RollW
 */
public interface ContainerFinder {
    String LOCAL = "[LOCAL]";

    boolean dataExists(String fileId, String source);

    @Nullable
    Container findContainer(String containerId, long serial, String source);

    @Nullable
    ContainerGroup findContainerGroup(String containerId, String source);

    @NonNull
    List<Container> findContainersByFile(String fileId, String source);

    ContainerGroup findContainerGroupByFile(String fileId, String source);

    static boolean isLocal(String source) {
        if (source == null) {
            return false;
        }
        return source.equals(LOCAL);
    }
}
