package org.huel.cloudhub.client.service.rpc;

import io.grpc.ManagedChannel;
import io.grpc.stub.StreamObserver;
import org.huel.cloudhub.client.rpc.file.FileStatusResponse;
import org.huel.cloudhub.file.rpc.block.*;
import org.huel.cloudhub.rpc.GrpcServiceStubPool;
import org.huel.cloudhub.server.rpc.server.SerializedFileServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author RollW
 */
@Service
public class ClientFileDownloadService {
    private final Logger logger = LoggerFactory.getLogger(ClientFileDownloadService.class);
    private final FileServerChannelPool fileServerChannelPool;
    private final GrpcServiceStubPool<BlockDownloadServiceGrpc.BlockDownloadServiceStub>
            blockDownloadServiceStubPool;
    private final ClientFileStatusService clientFileStatusService;

    public ClientFileDownloadService(FileServerChannelPool fileServerChannelPool,
                                     ClientFileStatusService clientFileStatusService) {
        this.fileServerChannelPool = fileServerChannelPool;
        this.clientFileStatusService = clientFileStatusService;
        this.blockDownloadServiceStubPool = new GrpcServiceStubPool<>();
    }


    public void downloadFile(OutputStream outputStream, String fileId) throws FileDownloadingException {
        FileStatusResponse response =
                clientFileStatusService.getFileStatus(fileId);
        if (response.getServersList().isEmpty()) {
            throw new FileDownloadingException(FileDownloadingException.Type.SERVER_DOWN,
                    "No active servers");
        }

        SerializedFileServer first = response.getServersList().get(0);
        DownloadBlockRequest request = buildFirstRequest(first, response.getMasterId(), fileId);
        BlockDownloadServiceGrpc.BlockDownloadServiceStub stub =
                requireStub(first);

        logger.debug("Start downloading file, id={}.", fileId);
        stub.downloadBlocks(request, new DownloadBlockStreamObserver(outputStream));
    }

    private DownloadBlockRequest buildFirstRequest(SerializedFileServer server, String masterId, String fileId) {
        if (!server.getId().equals(masterId)) {
            return DownloadBlockRequest.newBuilder()
                    .setFileId(fileId)
                    .setSourceId(masterId)
                    .build();
        }
        return DownloadBlockRequest.newBuilder()
                .setFileId(fileId)
                .build();
    }

    private BlockDownloadServiceGrpc.BlockDownloadServiceStub requireStub(SerializedFileServer server) {
        ManagedChannel channel = fileServerChannelPool.getChannel(server);
        BlockDownloadServiceGrpc.BlockDownloadServiceStub stub =
                blockDownloadServiceStubPool.getStub(server.getId());
        if (stub != null) {
            return stub;
        }
        stub = BlockDownloadServiceGrpc.newStub(channel);
        blockDownloadServiceStubPool.registerStub(server.getId(), stub);
        return stub;
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
                logger.error("--- Not a valid response.");
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
                throw new FileDownloadingException(FileDownloadingException.Type.DATA_LOSS,
                        "Illegal receive count.");
            }
            DownloadBlocksInfo downloadBlocksInfo = value.getDownloadBlocks();
            List<DownloadBlockData> dataList = downloadBlocksInfo.getDataList();
            logger.debug("Receive download response:index={}, count={}, dataBlock size={}",
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
            logger.debug("Download file complete. all request count: {}", receiveCount.get() - 1);
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
