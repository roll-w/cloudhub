package org.huel.cloudhub.file.fs.container;

import org.huel.cloudhub.file.fs.ServerFile;
import org.huel.cloudhub.file.fs.container.replica.ReplicaContainerNameMeta;
import org.huel.cloudhub.file.io.RepresentFile;

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
