package org.huel.cloudhub.file.server.service.container;

import org.huel.cloudhub.file.fs.FileServer;
import org.huel.cloudhub.file.fs.ServerFile;
import org.huel.cloudhub.file.fs.container.ContainerLocation;
import org.huel.cloudhub.file.fs.meta.ContainerLocator;
import org.huel.cloudhub.file.fs.meta.ContainerMeta;
import org.huel.cloudhub.file.fs.meta.ContainerMetaFactory;
import org.huel.cloudhub.file.fs.meta.MetadataCacheStrategy;
import org.huel.cloudhub.file.fs.meta.MetadataCacheable;
import org.huel.cloudhub.file.fs.meta.MetadataException;
import org.huel.cloudhub.file.fs.meta.MetadataLoader;

import java.io.IOException;

/**
 * @author RollW
 */
public class ContainerMetadataLoader implements MetadataLoader {
    private final ContainerMetaFactory containerMetaFactory;
    private MetadataCacheStrategy metadataCacheStrategy;
    private final ServerFile dataDirectory;
    private final FileServer fileServer;

    public ContainerMetadataLoader(ContainerMetaFactory containerMetaFactory,
                                   ServerFile dataDirectory,
                                   FileServer fileServer) {
        this.containerMetaFactory = containerMetaFactory;
        this.dataDirectory = dataDirectory;
        this.fileServer = fileServer;
    }

    @Override
    public ContainerMeta loadContainerMeta(ContainerLocator containerLocator)
            throws IOException, MetadataException {
        ServerFile metaFile = fileServer.getServerFileProvider()
                .openFile(dataDirectory, containerLocator.getLocator() + ContainerLocation.META_SUFFIX);
        MetadataCacheable<ContainerMeta> containerMetaMetadataCacheable =
                metadataCacheStrategy.get(containerLocator.getLocator());
        if (containerMetaMetadataCacheable != null) {
            return containerMetaMetadataCacheable.getMeta();
        }
        ContainerMeta containerMeta =
                containerMetaFactory.loadContainerMeta(metaFile, containerLocator);
        metadataCacheStrategy.cache(containerMeta);

        return containerMeta;
    }

    @Override
    public void setMetadataCacheStrategy(MetadataCacheStrategy strategy) {
        this.metadataCacheStrategy = strategy;
    }
}
