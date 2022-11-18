package org.huel.cloudhub.meta.server.service.file;

import io.grpc.ManagedChannel;
import io.grpc.stub.StreamObserver;
import org.huel.cloudhub.file.rpc.block.*;
import org.huel.cloudhub.meta.server.data.database.repository.FileStorageLocationRepository;
import org.huel.cloudhub.meta.server.data.entity.FileStorageLocation;
import org.huel.cloudhub.meta.server.service.node.*;
import org.huel.cloudhub.rpc.GrpcProperties;
import org.huel.cloudhub.rpc.GrpcServiceStubPool;
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
    private final Logger logger = LoggerFactory.getLogger(FileDownloadService.class);

    private final NodeAllocator nodeAllocator;
    private final ServerChecker serverChecker;
    private final FileStorageLocationRepository repository;
    private final NodeChannelPool nodeChannelPool;
    private final GrpcServiceStubPool<BlockDownloadServiceGrpc.BlockDownloadServiceStub>
            blockDownloadServiceStubPool;

    public FileDownloadService(HeartbeatService heartbeatService,
                               FileStorageLocationRepository repository,
                               GrpcProperties grpcProperties) {
        this.nodeAllocator = heartbeatService.getNodeAllocator();
        this.serverChecker = heartbeatService.getServerChecker();
        this.repository = repository;
        this.nodeChannelPool = new NodeChannelPool(grpcProperties);
        this.blockDownloadServiceStubPool = new GrpcServiceStubPool<>();
    }

    public void downloadFile(OutputStream outputStream, String fileId) {
        FileStorageLocation location = repository.getByFileId(fileId);
        if (location == null) {
            throw new FileDownloadingException("Failed downloading file: file not exists. file_id=" + fileId);
        }
        List<RequestServer> activeServers = tryGetAllActiveServers(location);
        if (activeServers.isEmpty()) {
            throw new FileDownloadingException("Failed downloading file: no active server. file_id=" + fileId);
        }
        RequestServer first = activeServers.get(0);
        // TODO: load balance. fragment request to the other servers.

        DownloadBlockRequest request = buildFirstRequest(first, location, fileId);
        BlockDownloadServiceGrpc.BlockDownloadServiceStub stub =
                requireStub(first.server());

        logger.debug("Start downloading file id={}", fileId);
        // TODO: async here, needs callback
        stub.downloadBlocks(request, new DownloadBlockStreamObserver(outputStream));
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

    private BlockDownloadServiceGrpc.BlockDownloadServiceStub requireStub(NodeServer server) {
        ManagedChannel channel = nodeChannelPool.getChannel(server);
        BlockDownloadServiceGrpc.BlockDownloadServiceStub stub =
                blockDownloadServiceStubPool.getStub(server.id());
        if (stub != null) {
            return stub;
        }
        stub = BlockDownloadServiceGrpc.newStub(channel);
        blockDownloadServiceStubPool.registerStub(server.id(), stub);
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
            NodeServer server = nodeAllocator.findNodeServer(id);
            if (server == null) {
                return;
            }
            requestServers.add(new RequestServer(server, serverType));
        });

        return requestServers;
    }

    private class DownloadBlockStreamObserver implements StreamObserver<DownloadBlockResponse> {
        private int responseCount;
        private long fileLength;
        private long validBytes;
        private final OutputStream outputStream;
        private final AtomicInteger receiveCount = new AtomicInteger(1);

        private DownloadBlockStreamObserver(OutputStream outputStream) {
            this.outputStream = outputStream;
        }

        private void saveCheckMessage(DownloadBlockResponse.CheckMessage checkMessage) {
            // TODO:
            logger.debug("receive first download response, request count: {}", checkMessage.getResponseCount());
            responseCount = checkMessage.getResponseCount();
            fileLength = checkMessage.getFileLength();
            validBytes = checkMessage.getValidBytes();
        }

        @Override
        public void onNext(DownloadBlockResponse value) {
            if (value.getDownloadMessageCase() ==
                    DownloadBlockResponse.DownloadMessageCase.DOWNLOADMESSAGE_NOT_SET) {
                logger.error("--- not a valid response.");
                throw new IllegalArgumentException("Not valid response.");
            }
            if (value.getDownloadMessageCase() == DownloadBlockResponse.DownloadMessageCase.FILE_EXISTS) {
                logger.debug("File not exists");
                onFileNotExists();
                return;
            }

            if (value.getDownloadMessageCase() ==
                    DownloadBlockResponse.DownloadMessageCase.CHECK_MESSAGE) {
                saveCheckMessage(value.getCheckMessage());
                return;
            }
            if (receiveCount.get() > responseCount) {
                throw new IllegalStateException("Illegal receive count.");
            }
            DownloadBlocksInfo downloadBlocksInfo = value.getDownloadBlocks();
            List<DownloadBlockData> dataList = downloadBlocksInfo.getDataList();
            logger.debug("receive download response:index={}, count={}, dataBlock size={}",
                    downloadBlocksInfo.getIndex(), receiveCount.get(), dataList.size());
            writeTo(dataList, outputStream, calcValidBytes(receiveCount.get()));

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
            logger.error("download file error.", t);
            try {
                outputStream.close();
            } catch (IOException e) {
                logger.debug("close error.", e);
            }
        }

        @Override
        public void onCompleted() {
            // TODO: check file.
            logger.debug("download file complete. all request count: {}", receiveCount.get() - 1);
            try {
                outputStream.close();
            } catch (IOException e) {
                logger.debug("close error.", e);
            }
        }

        private void onFileNotExists() {
            // TODO: file not exists, choose other server
        }
    }

    private void writeTo(List<DownloadBlockData> downloadBlockData, OutputStream stream, long validBytes) {
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
            throw new RuntimeException(e);
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
            throw new RuntimeException(e);
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
