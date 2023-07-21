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

import org.cloudhub.file.fs.ServerFile;
import org.cloudhub.file.fs.container.replica.ReplicaContainerNameMeta;
import org.cloudhub.file.io.RepresentFile;

import java.io.File;
import java.util.Objects;

/**
 * A container location is a container's path in the file system.
 *
 * @author RollW
 */
public class ContainerLocation implements RepresentFile {
    public static final String META_SUFFIX = ".meta";
    public static final String REPLICA_META_SUFFIX = ".meta";

    private final String childPath;
    private final String dirPath;
    private final String dataPath;
    private final String metaSuffix;

    public ContainerLocation(String dataPath) {
        this(dataPath, META_SUFFIX);
    }

    public ContainerLocation(String dataPath, String metaSuffix) {
        this.metaSuffix = metaSuffix;
        this.dataPath = dataPath;
        int idx = dataPath.lastIndexOf(File.separatorChar);
        this.dirPath = dataPath.substring(0, idx);
        this.childPath = dataPath.substring(idx + 1);
    }

    private ContainerLocation(String dir, String child, String meta) {
        this.metaSuffix = meta;
        this.dataPath = dir + File.separator + child;
        this.dirPath = dir;
        this.childPath = child;
    }

    public String getMetaSuffix() {
        return metaSuffix;
    }

    public String getResourceLocator() {
        return childPath;
    }

    @Override
    public String getLocalPath() {
        return dataPath;
    }

    @Override
    public File toFile() {
        return new File(dataPath);
    }

    @Override
    public boolean exists() {
        return toFile().exists();
    }

    public String getMetaPath() {
        return dataPath + META_SUFFIX;
    }

    public static String toDataPath(ServerFile dir, String path) {
        return dir.getPath() + File.separator + path;
    }

    public static String toDataPath(String parent, String path) {
        return parent + File.separator + path;
    }

    public ContainerLocation fork(String childPath) {
        return new ContainerLocation(dirPath, childPath, metaSuffix);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContainerLocation location = (ContainerLocation) o;
        return Objects.equals(childPath, location.childPath) && Objects.equals(dirPath, location.dirPath) && Objects.equals(dataPath, location.dataPath) && Objects.equals(metaSuffix, location.metaSuffix);
    }

    @Override
    public int hashCode() {
        return Objects.hash(childPath, dirPath, dataPath, metaSuffix);
    }

    @Override
    public String toString() {
        return "ContainerLocation{" +
                "childPath='" + childPath + '\'' +
                ", dirPath='" + dirPath + '\'' +
                ", dataPath='" + dataPath + '\'' +
                ", metaSuffix='" + metaSuffix + '\'' +
                '}';
    }

    public static String toContainerName(String id, String source, long serial) {
        if (ContainerFinder.isLocal(source)) {
            return new ContainerNameMeta(id, serial).getName();
        }
        return new ReplicaContainerNameMeta(source, id, serial).getName();
    }
}
