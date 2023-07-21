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

import org.cloudhub.file.fs.container.ContainerNameMeta;
import org.cloudhub.file.fs.container.replica.ReplicaContainerNameMeta;
import org.cloudhub.file.fs.container.ContainerIdentity;
import org.cloudhub.file.fs.container.ContainerNameMeta;
import org.cloudhub.file.fs.container.replica.ReplicaContainerNameMeta;
import org.cloudhub.file.server.service.file.FileUtils;

import java.util.regex.Pattern;

/**
 * @author RollW
 */
public final class ContainerMetaKeys {
    public static final String CONTAINER_META_SUFFIX = ".cmeta";

    // contains no dot(.)
    private static final String CONTAINER_META_SUFFIX_N = "cmeta";

    public static final String REPLICA_CONTAINER_META_SUFFIX = ".rcmeta";

    private static final String REPLICA_CONTAINER_META_SUFFIX_N = "rcmeta";

    public static String toMetaFileName(String containerLocator) {
        if (containerLocator == null) {
            throw new NullPointerException();
        }
        if (ReplicaContainerNameMeta.check(containerLocator)) {
            ReplicaContainerNameMeta containerNameMeta =
                    ReplicaContainerNameMeta.parse(containerLocator);
            return ContainerIdentity.toCmetaId(containerNameMeta.getId()) + REPLICA_CONTAINER_META_SUFFIX;
        }
        ContainerNameMeta containerNameMeta = ContainerNameMeta.parse(containerLocator);
        return ContainerIdentity.toCmetaId(containerNameMeta.getId()) + CONTAINER_META_SUFFIX;
    }

    public static boolean isMetaFile(String fileName) {
        if (fileName == null) {
            throw new NullPointerException();
        }
        final String extName = FileUtils.getExtensionName(fileName);
        if (CONTAINER_META_SUFFIX_N.equalsIgnoreCase(extName)) {
            return true;
        }
        return REPLICA_CONTAINER_META_SUFFIX_N.equalsIgnoreCase(extName);
    }

    public static boolean isReplicaMetaFile(String metaFileName) {
        if (metaFileName == null) {
            throw new NullPointerException();
        }
        final String extName = FileUtils.getExtensionName(metaFileName);
        if (REPLICA_CONTAINER_META_SUFFIX_N.equalsIgnoreCase(extName)) {
            return true;
        }
        String[] parts = metaFileName.split(Pattern.quote("_"));
        return parts.length == 3;
    }

    private ContainerMetaKeys() {
    }
}
