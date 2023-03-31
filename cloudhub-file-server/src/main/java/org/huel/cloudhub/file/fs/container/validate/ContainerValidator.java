package org.huel.cloudhub.file.fs.container.validate;

import org.huel.cloudhub.file.fs.FileServer;
import org.huel.cloudhub.file.fs.ServerFile;
import org.huel.cloudhub.file.fs.container.ChecksumCalculator;
import org.huel.cloudhub.file.fs.meta.ContainerGroupMeta;
import org.huel.cloudhub.file.fs.meta.ContainerMeta;
import org.huel.cloudhub.file.fs.meta.MetadataException;
import org.huel.cloudhub.file.fs.meta.MetadataLoader;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Validate a container with its metadata.
 *
 * @author RollW
 */
public class ContainerValidator {
    private final ServerFile dataDirectory;
    private final FileServer fileServer;
    private final String locator;
    private final ContainerGroupMeta containerGroupMeta;
    private final MetadataLoader metadataLoader;

    public ContainerValidator(ServerFile dataDirectory,
                              FileServer fileServer,
                              String locator,
                              ContainerGroupMeta containerGroupMeta,
                              MetadataLoader metadataLoader) {
        this.dataDirectory = dataDirectory;
        this.fileServer = fileServer;
        this.locator = locator;
        this.containerGroupMeta = containerGroupMeta;
        this.metadataLoader = metadataLoader;
    }

    public ContainerStatues validate(ChecksumCalculator checksumCalculator)
            throws IOException {
        Set<ContainerStatus> containerStatuses = check(checksumCalculator);
        boolean valid = containerStatuses.contains(ContainerStatus.VALID);
        return new ContainerStatues(containerStatuses, valid);
    }

    private Set<ContainerStatus> check(ChecksumCalculator checksumCalculator) throws IOException {
        Set<ContainerStatus> containerStatuses = new HashSet<>();
        ServerFile containerFile = fileServer.getServerFileProvider()
                .openFile(dataDirectory, locator);
        ContainerMeta containerMeta = null;
        try {
            containerMeta = tryLoadContainerMeta();
            if (containerMeta == null) {
                containerStatuses.add(ContainerStatus.META_LOST);
            }
        } catch (MetadataException e) {
            e.printStackTrace();
            containerStatuses.add(ContainerStatus.META_FAILED);
        }

        boolean containerExists  = containerFile.exists();
        if (!containerExists) {
            containerStatuses.add(ContainerStatus.CONTAINER_LOST);
        }
        if (containerMeta == null) {
            // unable to check checksum
            return containerStatuses;
        }
        String containerChecksum = containerMeta.getChecksum();
        String calcedChecksum = checksumCalculator.calculateChecksum(containerFile);

        if (!containerChecksum.equals(calcedChecksum)) {
            containerStatuses.add(ContainerStatus.CONTAINER_LOST);
        }
        if (containerStatuses.isEmpty()) {
            containerStatuses.add(ContainerStatus.VALID);
        }
        return containerStatuses;
    }

    private ContainerMeta tryLoadContainerMeta() throws IOException, MetadataException {
        ServerFile metaFile = fileServer.getServerFileProvider()
                .openFile(dataDirectory, locator + ".meta");
        if (!metaFile.exists()) {
            return null;
        }
        return containerGroupMeta.loadChildContainerMeta(
                metadataLoader, locator);
    }
}
