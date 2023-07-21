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

package org.cloudhub.file.server.service.container;

import org.cloudhub.file.diagnosis.Diagnosable;
import org.cloudhub.file.diagnosis.DiagnosisRecorder;
import org.cloudhub.file.diagnosis.DiagnosisReportSegment;
import org.cloudhub.file.diagnosis.SimpleDiagnosisRecorder;
import org.cloudhub.file.fs.LocalFileServer;
import org.cloudhub.file.fs.ServerFile;
import org.cloudhub.file.fs.block.BlockMetaInfo;
import org.cloudhub.file.fs.block.FileBlockMetaInfo;
import org.cloudhub.file.fs.container.*;
import org.cloudhub.file.fs.container.validate.ContainerStatues;
import org.cloudhub.file.fs.container.validate.ContainerValidator;
import org.cloudhub.file.fs.meta.*;
import org.cloudhub.file.server.service.ContainerDiagnosable;
import org.cloudhub.server.rpc.status.SerializedContainerStatus;
import org.cloudhub.server.rpc.status.SerializedContainerType;
import org.cloudhub.server.rpc.status.SerializedDamagedContainerReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author RollW
 */
@SuppressWarnings("all")
@Service
public class MetaContainerService implements ContainerDiagnosable {
    private static final Logger logger = LoggerFactory.getLogger(MetaContainerService.class);

    private final Map<String, SourcedContainerGroup> sourcedContainerGroupMap =
            new ConcurrentHashMap<>();
    private final ContainerMetaFactory containerMetaFactory;
    private final ChecksumCalculator checksumCalculator;
    private final MetadataLoader metadataLoader;
    private final ServerFile dataDirectory;
    private final ServerFile metaDirectory;
    private final LocalFileServer localFileServer;
    private final DiagnosisRecorder<SerializedDamagedContainerReport> diagnosisRecorder =
            new SimpleDiagnosisRecorder();


    public MetaContainerService(ChecksumCalculator checksumCalculator,
                                LocalFileServer localFileServer,
                                ContainerProperties containerProperties) {
        this.checksumCalculator = checksumCalculator;
        this.containerMetaFactory = new ContainerMetaFactory();
        this.dataDirectory = localFileServer.getServerFileProvider()
                .openFile(containerProperties.getContainerPath());
        this.metaDirectory = localFileServer.getServerFileProvider()
                .openFile(containerProperties.getMetaPath());
        this.metadataLoader = new ContainerMetadataLoader(
                containerMetaFactory,
                dataDirectory,
                localFileServer
        );

        this.localFileServer = localFileServer;

        try {
            loadContainers(metaDirectory);
        } catch (IOException | MetadataException e) {
            logger.error("Load containers error", e);
        }
    }

    private void loadContainers(ServerFile metaDirectory)
            throws IOException, MetadataException {
        List<ServerFile> metaFiles = metaDirectory.listFiles();
        List<BrokenContainer> allBrokenLocators = new ArrayList<>();

        for (ServerFile metaFile : metaFiles) {
            final String fileName = metaFile.getName();
            if (!ContainerMetaKeys.isMetaFile(fileName)) {
                logger.debug("Detected non-meta file: {} in the meta directory.",
                        fileName);
                continue;
            }
            ContainerGroupMeta containerGroupMeta =
                    containerMetaFactory.loadContainerGroupMeta(metaFile);
            List<BrokenContainer> brokenLocators =
                    checkChildContainers(containerGroupMeta);
            if (!brokenLocators.isEmpty()) {
                allBrokenLocators.addAll(brokenLocators);
            }
            final String source = containerGroupMeta.getSource();
            SourcedContainerGroup sourcedContainerGroup = loadOrCreateGroup(source);
            try {
                List<? extends ContainerMeta> containerMetas =
                        containerGroupMeta.loadChildContainerMeta(metadataLoader);
                loadIntoGroup(sourcedContainerGroup, containerMetas);
            } catch (MetadataException e) {
                continue;
            }
        }

        if (!allBrokenLocators.isEmpty()) {
            logger.warn("Broken containers: {}", allBrokenLocators);
            recordDiagnosisReport(allBrokenLocators);
        }
    }

    private void recordDiagnosisReport(List<BrokenContainer> brokenContainers) {
        List<SerializedContainerStatus> serializedContainerStatuses = new ArrayList<>();

        brokenContainers.forEach(brokenContainer -> {
            SerializedContainerType containerType = ContainerFinder.isLocal(
                    brokenContainer.containerLocator().getSource()
            ) ? SerializedContainerType.CONTAINER : SerializedContainerType.REPLICA;
            SerializedContainerStatus serializedContainerStatus =
                    SerializedContainerStatus.newBuilder()
                            .setContainerId(brokenContainer.containerLocator().getId())
                            .setType(containerType)
                            .setSerial(brokenContainer.containerLocator().getSerial())
                            .addAllCodes(brokenContainer.containerStatues().serialized())
                            .setSource(brokenContainer.containerLocator().getSource())
                            .build();
            FileCheck fileCheck = checkFile(
                    brokenContainer.containerLocator().getId(),
                    brokenContainer.containerLocator().getSerial(),
                    brokenContainer.containerLocator().getSource(),
                    brokenContainer.containerStatues()
            );
            SerializedDamagedContainerReport report = SerializedDamagedContainerReport
                    .newBuilder()
                    .setStatus(serializedContainerStatus)
                    .addAllAvaFileId(fileCheck.availableFiles())
                    .addAllDamFileId(fileCheck.damagedFiles())
                    .setAllFilesBroken(fileCheck.isDamaged())
                    .build();
            diagnosisRecorder.record(
                    new DiagnosisReportSegment<>(DiagnosisReportSegment.Type.DAMAGED, report)
            );
        });
    }

    private FileCheck checkFile(String containerId,
                                long serial,
                                String source,
                                ContainerStatues containerStatues) {
        if (!containerStatues.hasContainerBroken()) {
            return FileCheck.OK;
        }
        SourcedContainerGroup sourcedContainerGroup = loadOrCreateGroup(source);
        if (sourcedContainerGroup == null) {
            return FileCheck.UNAVALIABLE;
        }
        ContainerGroup containerGroup = sourcedContainerGroup.getGroup(containerId);
        if (containerGroup == null) {
            return FileCheck.UNAVALIABLE;
        }
        List<FileBlockMetaInfo> fileBlockMetaInfos =
                containerGroup.listFileBlockMetaInfos();
        List<String> damagedFiles = new ArrayList<>();
        List<String> availableFiles = new ArrayList<>();
        for (FileBlockMetaInfo fileBlockMetaInfo : fileBlockMetaInfos) {
            if (checkIfDamagedFile(fileBlockMetaInfo, serial)) {
                damagedFiles.add(fileBlockMetaInfo.getFileId());
            } else {
                availableFiles.add(fileBlockMetaInfo.getFileId());
            }
        }

        return damagedFiles.isEmpty()
                ? new FileCheck(availableFiles, damagedFiles, false)
                : new FileCheck(availableFiles, damagedFiles, true);
    }


    private boolean checkIfDamagedFile(FileBlockMetaInfo fileBlockMetaInfo,
                                       long serial) {
        for (BlockMetaInfo blockMetaInfo : fileBlockMetaInfo.getBlockMetaInfos()) {
            if (blockMetaInfo.getNextContainerSerial() == serial ||
                    blockMetaInfo.getContainerSerial() == serial) {
                return true;
            }
            // TODO: check length
        }
        return false;
    }


    public SourcedContainerGroup findGroup(String source) {
        return sourcedContainerGroupMap.get(source);
    }

    private List<BrokenContainer> checkChildContainers(ContainerGroupMeta containerGroupMeta) throws IOException {
        List<? extends ContainerLocator> containerLocators =
                containerGroupMeta.getChildLocators();
        List<BrokenContainer> brokenLocators = new ArrayList<>();
        for (ContainerLocator containerLocator : containerLocators) {
            ContainerValidator containerValidator = new ContainerValidator(
                    dataDirectory,
                    localFileServer,
                    containerLocator.getLocator(),
                    containerGroupMeta,
                    metadataLoader);
            ContainerStatues containerStatues =
                    containerValidator.validate(checksumCalculator);
            if (containerStatues.isValid()) {
                continue;
            }
            logger.warn("Broken container: {}, statues: {}.",
                    containerLocator, containerStatues.getContainerStatuses());
            BrokenContainer brokenContainer = new BrokenContainer(
                    containerStatues,
                    containerLocator
            );

            brokenLocators.add(brokenContainer);
        }
        return brokenLocators;
    }

    private void loadIntoGroup(SourcedContainerGroup sourcedContainerGroup,
                               List<? extends ContainerMeta> containerMetas) {
        for (ContainerMeta containerMeta : containerMetas) {
            ServerFile file = localFileServer.getServerFileProvider()
                    .openFile(dataDirectory, containerMeta.getLocator());
            ContainerLocation containerLocation = new ContainerLocation(
                    file.getPath(),
                    containerMeta.getContainerType().getMetaSuffix()
            );

            ContainerIdentity containerIdentity = new ContainerIdentity(
                    containerMeta.getId(),
                    containerMeta.getChecksum(),
                    containerMeta.getSerial(),
                    containerMeta.getBlockCapacity(),
                    containerMeta.getBlockSize()
            );

            List<BlockMetaInfo> blockMetaInfos = loadBlockMetaInfos(containerMeta);
            Container container = new Container(
                    containerLocation,
                    containerMeta.getSource(),
                    containerMeta.getUsedBlock(),
                    containerIdentity,
                    blockMetaInfos,
                    containerMeta.getVersion(),
                    true
            );
            ContainerGroup containerGroup =
                    sourcedContainerGroup.getOrCreateGroup(container.getId());
            containerGroup.put(container);
        }
    }

    private List<BlockMetaInfo> loadBlockMetaInfos(ContainerMeta containerMeta) {
        return (List<BlockMetaInfo>) containerMeta.getBlockFileMetas();
    }

    private SourcedContainerGroup loadOrCreateGroup(String source) {
        SourcedContainerGroup sourcedContainerGroup = sourcedContainerGroupMap.get(source);
        if (sourcedContainerGroup == null) {
            sourcedContainerGroup = new SourcedContainerGroup(source);
            sourcedContainerGroupMap.put(source, sourcedContainerGroup);
        }
        return sourcedContainerGroup;
    }

    @Override
    public Diagnosable<SerializedDamagedContainerReport> getDiagnosable() {
        return diagnosisRecorder;
    }

    private record BrokenContainer(
            ContainerStatues containerStatues,
            ContainerLocator containerLocator) {
    }

    private record FileCheck(
            List<String> availableFiles,
            List<String> damagedFiles,
            boolean isDamaged) {
        static final FileCheck OK = new FileCheck(List.of(), List.of(), false);
        static final FileCheck UNAVALIABLE = new FileCheck(List.of(), List.of(), true);
    }
}
