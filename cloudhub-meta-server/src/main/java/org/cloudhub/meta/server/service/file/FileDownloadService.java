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

package org.cloudhub.meta.server.service.file;

import io.grpc.ManagedChannel;
import io.grpc.stub.StreamObserver;
import org.cloudhub.file.rpc.block.*;
import org.cloudhub.meta.server.data.database.repository.FileStorageLocationRepository;
import org.cloudhub.meta.server.data.entity.FileStorageLocation;
import org.cloudhub.meta.server.service.node.*;
import org.cloudhub.rpc.GrpcServiceStubPool;
import org.cloudhub.rpc.StatusHelper;
import org.cloudhub.rpc.StreamObserverWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author RollW
 */
@Service
public class FileDownloadService {
    private static final Logger logger = LoggerFactory.getLogger(FileDownloadService.class);

    private final NodeAllocator nodeAllocator;
    private final ServerChecker serverChecker;
    private final FileStorageLocationRepository repository;
    private final NodeChannelPool nodeChannelPool;
    private final GrpcServiceStubPool<BlockDownloadServiceGrpc.BlockDownloadServiceStub>
            blockDownloadServiceStubPool;

    public FileDownloadService(HeartbeatService heartbeatService,
                               FileStorageLocationRepository repository,
                               NodeChannelPool nodeChannelPool) {
        this.nodeAllocator = heartbeatService.getNodeAllocator();
        this.serverChecker = heartbeatService.getServerChecker();
        this.repository = repository;
        this.nodeChannelPool = nodeChannelPool;
        this.blockDownloadServiceStubPool = new GrpcServiceStubPool<>();
    }

    public void downloadFile(OutputStream outputStream, String fileId, FileDownloadCallback callback) throws FileDownloadingException {
        FileStorageLocation location = repository.getByFileId(fileId);
        if (location == null) {
            throw new FileDownloadingException(FileDownloadingException.Type.NOT_EXIST,
                    "Failed downloading file: file not exists. file_id=" + fileId);
        }
        List<RequestServer> activeServers = tryGetAllActiveServers(location);
        if (activeServers.isEmpty()) {
            throw new FileDownloadingException(FileDownloadingException.Type.SERVER_DOWN,
                    "Failed downloading file: no active server. file_id=" + fileId);
        }
        RequestServer first = activeServers.get(0);
        // TODO: load balance. fragment request to the other servers.

        DownloadBlockRequest request = buildFirstRequest(first, location, fileId);
        BlockDownloadServiceGrpc.BlockDownloadServiceStub stub =
                requireStub(first.server());

        logger.debug("Start downloading file, id={}.", fileId);

        DownloadBlockStreamObserver streamObserver =
                new DownloadBlockStreamObserver(outputStream, callback);
        StreamObserver<DownloadBlockRequest> downloadBlockRequestStreamObserver =
                stub.downloadBlocks(streamObserver);
        streamObserver.setStreamObserver(downloadBlockRequestStreamObserver);
        downloadBlockRequestStreamObserver
                .onNext(request);
    }

    private DownloadBlockRequest buildFirstRequest(RequestServer server, FileStorageLocation location, String fileId) {
        if (server.serverType() == FileStorageLocation.ServerType.REPLICA) {
            return DownloadBlockRequest.newBuilder()
                    .setFileId(fileId)
                    .setSourceId(location.getMasterServerId())
                    .build();
        }
        return DownloadBlockRequest.newBuilder()
                .setFileId(fileId)
                .build();
    }

    private BlockDownloadServiceGrpc.BlockDownloadServiceStub requireStub(FileNodeServer server) {
        ManagedChannel channel = nodeChannelPool.getChannel(server);
        BlockDownloadServiceGrpc.BlockDownloadServiceStub stub =
                blockDownloadServiceStubPool.getStub(server.getId());
        if (stub != null) {
            return stub;
        }
        stub = BlockDownloadServiceGrpc.newStub(channel);
        blockDownloadServiceStubPool.registerStub(server.getId(), stub);
        return stub;
    }

    private List<RequestServer> tryGetAllActiveServers(FileStorageLocation location) {
        if (location == null) {
            return List.of();
        }
        List<RequestServer> requestServers = new ArrayList<>();
        location.getServersWithType((id, serverType) -> {
            if (!serverChecker.isActive(id)) {
                return;
            }
            FileNodeServer server = nodeAllocator.findNodeServer(id);
            if (server == null) {
                return;
            }
            requestServers.add(new RequestServer(server, serverType));
        });

        return requestServers;
    }

    private class DownloadBlockStreamObserver implements StreamObserver<DownloadBlockResponse> {
        private StreamObserverWrapper<DownloadBlockRequest> streamObserverWrapper;

        private int responseCount;
        private long fileLength;
        private long validBytes;
        private final OutputStream outputStream;
        private final FileDownloadCallback callback;
        private final AtomicInteger receiveCount = new AtomicInteger(1);

        private DownloadBlockStreamObserver(OutputStream outputStream,
                                            FileDownloadCallback callback) {
            this.outputStream = outputStream;
            this.callback = callback;
        }

        private void saveCheckMessage(DownloadBlockResponse.CheckMessage checkMessage) {
            // TODO:
            logger.debug("receive first download response, request count: {}", checkMessage.getResponseCount());
            responseCount = checkMessage.getResponseCount();
            fileLength = checkMessage.getFileLength();
            validBytes = checkMessage.getValidBytes();
        }

        void setStreamObserver(StreamObserver<DownloadBlockRequest> streamObserverWrapper) {
            this.streamObserverWrapper = new StreamObserverWrapper<>(streamObserverWrapper);
        }

        @Override
        public void onNext(DownloadBlockResponse value) {
            if (value.getDownloadMessageCase() ==
                    DownloadBlockResponse.DownloadMessageCase.DOWNLOADMESSAGE_NOT_SET) {
                throw new IllegalArgumentException("Not valid response.");
            }
            if (value.getDownloadMessageCase() ==
                    DownloadBlockResponse.DownloadMessageCase.FILE_EXISTS) {
                logger.debug("File not exists");
                onFileNotExists();
                return;
            }

            if (value.getDownloadMessageCase() ==
                    DownloadBlockResponse.DownloadMessageCase.CHECK_MESSAGE) {
                saveCheckMessage(value.getCheckMessage());
                if (callback != null) {
                    callback.onSaveCheckMessage(value.getCheckMessage());
                }
                return;
            }
            if (receiveCount.get() > responseCount) {
                FileDownloadingException e = new FileDownloadingException(FileDownloadingException.Type.DATA_LOSS,
                        "Illegal receive count.");
                if (callback != null) {
                    callback.onDownloadError(e);
                }
                throw e;
            }
            DownloadBlocksInfo downloadBlocksInfo = value.getDownloadBlocks();
            List<DownloadBlockData> dataList = downloadBlocksInfo.getDataList();
            logger.debug("Receive download response:index={}, count={}, dataBlock size={}",
                    downloadBlocksInfo.getIndex(), receiveCount.get(), dataList.size());
            try {
                writeTo(dataList, outputStream, calcValidBytes(receiveCount.get()));
            } catch (FileServerException e) {
                streamObserverWrapper.cancel();
                return;
            }

            receiveCount.incrementAndGet();
        }

        private long calcValidBytes(int index) {
            if (index == responseCount) {
                return validBytes;
            }
            return -1;
        }

        @Override
        public void onError(Throwable t) {
            if (StatusHelper.isCancelled(t)) {
                return;
            }
            logger.error("Download file error.", t);
            if (callback != null) {
                callback.onDownloadError(
                        new FileDownloadingException(FileDownloadingException.Type.OTHER, t.toString())
                );
            }
            try {
                outputStream.close();
            } catch (IOException e) {
            }
        }

        @Override
        public void onCompleted() {
            // TODO: check file.
            logger.debug("Download file complete. all request count: {}", receiveCount.get() - 1);
            if (callback != null) {
                callback.onDownloadComplete();
            }
            try {
                outputStream.close();
            } catch (IOException e) {
            }
        }

        private void onFileNotExists() {
            if (callback != null) {
                callback.onDownloadError(null);
            }
            // TODO: file not exists, choose other server
        }

    }

    private void writeTo(List<DownloadBlockData> downloadBlockData, OutputStream stream, long validBytes)
            throws FileServerException {
        if (validBytes < 0) {
            writeFully(downloadBlockData, stream);
            return;
        }

        try {
            final int size = downloadBlockData.size() - 1;
            int index = 0;
            for (DownloadBlockData downloadBlockDatum : downloadBlockData) {
                byte[] data = downloadBlockDatum.getData().toByteArray();
                long len = index == size ? validBytes : -1;
                writeOffset(stream, data, len);
                index++;
            }
            stream.flush();
        } catch (IOException e) {
            throw new FileServerException(e);
        }
    }

    private void writeFully(List<DownloadBlockData> downloadBlockData, OutputStream stream) {
        try {
            for (DownloadBlockData downloadBlockDatum : downloadBlockData) {
                byte[] data = downloadBlockDatum.getData().toByteArray();
                writeOffset(stream, data, -1);
            }
            stream.flush();
        } catch (IOException e) {
            throw new FileServerException(e);
        }
    }

    private void writeOffset(OutputStream stream, byte[] data, long len) throws IOException {
        if (len < 0) {
            stream.write(data);
            return;
        }
        stream.write(data, 0, (int) len);
    }
}
