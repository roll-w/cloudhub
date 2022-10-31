package org.huel.cloudhub.meta.server.service.file;

import io.grpc.ManagedChannel;
import io.grpc.stub.StreamObserver;
import org.huel.cloudhub.file.rpc.block.*;
import org.huel.cloudhub.meta.server.service.node.HeartbeatService;
import org.huel.cloudhub.meta.server.service.node.NodeChannelPool;
import org.huel.cloudhub.server.file.FileProperties;
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
public class FileDownloadService {
    private final Logger logger = LoggerFactory.getLogger(FileDownloadService.class);

    private final HeartbeatService heartbeatService;
    private final FileProperties fileProperties;
    private final NodeChannelPool nodeChannelPool;

    public FileDownloadService(HeartbeatService heartbeatService,
                               FileProperties fileProperties) {
        this.heartbeatService = heartbeatService;
        this.fileProperties = fileProperties;
        this.nodeChannelPool = new NodeChannelPool(fileProperties);
    }

    public void downloadFile(OutputStream outputStream, String fileId) {
        DownloadBlockRequest request = DownloadBlockRequest.newBuilder()
                .setFileId(fileId)
                .build();
        BlockDownloadServiceGrpc.BlockDownloadServiceStub stub =
                requireStub(fileId);

        logger.info("start downloading file id={}", fileId);
        stub.downloadBlocks(request, new DownloadBlockStreamObserver(outputStream));
    }

    private BlockDownloadServiceGrpc.BlockDownloadServiceStub requireStub(String fileId) {
        ManagedChannel channel = nodeChannelPool.getChannel(
                heartbeatService.randomServer());
        // TODO: replace with {NodeAllocator}

        BlockDownloadServiceGrpc.BlockDownloadServiceStub stub =
                BlockDownloadServiceGrpc.newStub(channel);
        // TODO: caching stub
        return stub;
    }

    private class DownloadBlockStreamObserver implements StreamObserver<DownloadBlockResponse> {
        private int responseCount;
        private long fileLength;
        private final OutputStream outputStream;
        private final AtomicInteger receiveCount = new AtomicInteger(0);
        private DownloadBlockStreamObserver(OutputStream outputStream) {
            this.outputStream = outputStream;
        }


        private void saveCheckMessage(DownloadBlockResponse.CheckMessage checkMessage) {
            // TODO:
            logger.info("receive first download response, request count: {}", checkMessage.getResponseCount());
            responseCount = checkMessage.getResponseCount();
            fileLength = checkMessage.getFileLength();
        }

        @Override
        public void onNext(DownloadBlockResponse value) {
            if (value.getDownloadMessageCase() ==
                    DownloadBlockResponse.DownloadMessageCase.DOWNLOADMESSAGE_NOT_SET) {
                logger.error("--- not a valid response.");
                throw new IllegalArgumentException("Not valid response.");
            }
            if (value.getDownloadMessageCase() ==
                    DownloadBlockResponse.DownloadMessageCase.CHECK_MESSAGE) {
                saveCheckMessage(value.getCheckMessage());
                return;
            }
            if (receiveCount.get() >= responseCount) {
                throw new IllegalStateException("Illegal receive count.");
            }
            DownloadBlocksInfo downloadBlocksInfo = value.getDownloadBlocks();
            List<DownloadBlockData> dataList = downloadBlocksInfo.getDataList();
            receiveCount.incrementAndGet();
            writeTo(dataList, outputStream);
        }

        @Override
        public void onError(Throwable t) {
            logger.error("download file error.", t);

            try {
                outputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void onCompleted() {
            // TODO: check file.
            logger.info("download file complete.");
            try {
                outputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }


    }

    private void writeTo(List<DownloadBlockData> downloadBlockData, OutputStream stream)  {
        try {
            for (DownloadBlockData downloadBlockDatum : downloadBlockData) {
                stream.write(downloadBlockDatum.getData().toByteArray());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}