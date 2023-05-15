package org.huel.cloudhub.client;

import io.grpc.ManagedChannel;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.huel.cloudhub.client.rpc.file.FileStatusResponse;
import org.huel.cloudhub.file.rpc.block.BlockDownloadServiceGrpc;
import org.huel.cloudhub.file.rpc.block.DownloadBlockData;
import org.huel.cloudhub.file.rpc.block.DownloadBlockRequest;
import org.huel.cloudhub.file.rpc.block.DownloadBlockResponse;
import org.huel.cloudhub.file.rpc.block.DownloadBlocksInfo;
import org.huel.cloudhub.file.rpc.block.DownloadBlocksSegmentInfo;
import org.huel.cloudhub.file.rpc.block.DownloadBytesSegment;
import org.huel.cloudhub.rpc.GrpcServiceStubPool;
import org.huel.cloudhub.rpc.StatusHelper;
import org.huel.cloudhub.rpc.StreamObserverWrapper;
import org.huel.cloudhub.server.rpc.server.SerializedFileServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author RollW
 */
public class ClientFileDownloadClient {
    private static final Logger logger = LoggerFactory.getLogger(ClientFileDownloadClient.class);
    private final FileServerChannelPool fileServerChannelPool;
    private final GrpcServiceStubPool<BlockDownloadServiceGrpc.BlockDownloadServiceStub>
            blockDownloadServiceStubPool;
    private final MetaFileStatusClient metaFileStatusClient;

    private static final String APACHE_ABORT_EXCEPTION =
            "org.apache.catalina.connector.ClientAbortException";

    private static final String[] PASSED_EXCEPTIONS = {
            APACHE_ABORT_EXCEPTION
    };

    private static boolean isPassedException(Throwable throwable) {
        String name = throwable.getClass().getCanonicalName();
        for (String passedException : PASSED_EXCEPTIONS) {
            if (name.equals(passedException)) {
                return true;
            }
        }
        return false;
    }

    public ClientFileDownloadClient(FileServerChannelPool fileServerChannelPool,
                                    MetaFileStatusClient metaFileStatusClient) {
        this.fileServerChannelPool = fileServerChannelPool;
        this.metaFileStatusClient = metaFileStatusClient;
        this.blockDownloadServiceStubPool = new GrpcServiceStubPool<>();
    }

    public void downloadFile(OutputStream outputStream, String fileId,
                             ClientFileDownloadCallback callback) throws FileDownloadingException {
        FileStatusResponse response =
                metaFileStatusClient.getFileStatus(fileId);
        if (response.getServersList().isEmpty()) {
            throw new FileDownloadingException(FileDownloadingException.Type.SERVER_DOWN,
                    "No active servers");
        }

        SerializedFileServer first = response.getServersList().get(0);
        DownloadBlockRequest request = buildFirstRequest(first, response.getMasterId(), fileId);
        BlockDownloadServiceGrpc.BlockDownloadServiceStub stub =
                requireStub(first);

        logger.debug("Start downloading file, id={}.", fileId);
        DownloadBlockStreamObserver downloadBlockStreamObserver =
                new DownloadBlockStreamObserver(callback, outputStream);
        StreamObserver<DownloadBlockRequest> requestStreamObserver =
                stub.downloadBlocks(downloadBlockStreamObserver);
        downloadBlockStreamObserver.setStreamObserver(requestStreamObserver);
        requestStreamObserver.onNext(request);
    }


    public void downloadFile(OutputStream outputStream, String fileId,
                             long startBytes, long endBytes,
                             ClientFileDownloadCallback callback) throws FileDownloadingException {
        FileStatusResponse response =
                metaFileStatusClient.getFileStatus(fileId);
        if (response.getServersList().isEmpty()) {
            throw new FileDownloadingException(FileDownloadingException.Type.SERVER_DOWN,
                    "No active servers.");
        }
        SerializedFileServer first = response.getServersList().get(0);
        DownloadBlockRequest request = buildFirstRequest(
                first, response.getMasterId(),
                fileId, startBytes, endBytes);
        BlockDownloadServiceGrpc.BlockDownloadServiceStub stub =
                requireStub(first);
        BlockDownloadServiceGrpc.BlockDownloadServiceBlockingStub serviceBlockingStub;

        logger.debug("Start downloading file, id={}.", fileId);
        DownloadBlockStreamObserver downloadBlockStreamObserver =
                new DownloadBlockStreamObserver(callback, outputStream);
        StreamObserver<DownloadBlockRequest> requestStreamObserver =
                stub.downloadBlocks(downloadBlockStreamObserver);
        downloadBlockStreamObserver.setStreamObserver(requestStreamObserver);
        requestStreamObserver.onNext(request);
    }

    private DownloadBlockRequest buildFirstRequest(SerializedFileServer server, String masterId, String fileId,
                                                   long startBytes, long endBytes) {
        DownloadBytesSegment bytesSegment = DownloadBytesSegment.newBuilder()
                .setStartBytes(startBytes)
                .setEndBytes(endBytes)
                .build();
        DownloadBlockRequest.Builder builder = DownloadBlockRequest.newBuilder()
                .setFileId(fileId)
                .setSegmentInfo(DownloadBlocksSegmentInfo
                        .newBuilder()
                        .setBytes(bytesSegment)
                        .build()
                );
        if (!server.getId().equals(masterId)) {
            return builder.setSourceId(masterId)
                    .build();
        }
        return builder.build();
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

    private static class DownloadBlockStreamObserver implements StreamObserver<DownloadBlockResponse> {
        private StreamObserverWrapper<DownloadBlockRequest> requestStreamObserverWrapper;

        private final ClientFileDownloadCallback callback;

        private int responseCount;
        private long fileLength;
        private long validBytes;
        private final OutputStream outputStream;
        private final AtomicInteger receiveCount = new AtomicInteger(1);

        private DownloadBlockStreamObserver(ClientFileDownloadCallback callback,
                                            OutputStream outputStream) {
            this.callback = callback;
            this.outputStream = outputStream;
        }

        void setStreamObserver(StreamObserver<DownloadBlockRequest> streamObserver) {
            this.requestStreamObserverWrapper =
                    new StreamObserverWrapper<>(streamObserver);
        }

        private void saveCheckMessage(DownloadBlockResponse.CheckMessage checkMessage) {
            logger.debug("receive first download response, request count: {}",
                    checkMessage.getResponseCount());
            responseCount = checkMessage.getResponseCount();
            fileLength = checkMessage.getFileLength();
            validBytes = checkMessage.getValidBytes();
        }

        @Override
        public void onNext(DownloadBlockResponse value) {
            if (value.getDownloadMessageCase() ==
                    DownloadBlockResponse.DownloadMessageCase.DOWNLOADMESSAGE_NOT_SET) {
                logger.error("Not a valid response.");
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
                logger.error("Illegal receive count.");
                requestStreamObserverWrapper.cancel();
                onCompleteCallback(CFSStatus.DATA_LOSS);
                return;
            }

            DownloadBlocksInfo downloadBlocksInfo = value.getDownloadBlocks();
            List<DownloadBlockData> dataList = downloadBlocksInfo.getDataList();
            logger.debug("Receive download response: index={}, count={}, dataBlock size={}",
                    downloadBlocksInfo.getIndex(), receiveCount.get(), dataList.size());
            receiveCount.incrementAndGet();
            try {
                writeTo(dataList, outputStream, -1);
            } catch (IOException e) {
                requestStreamObserverWrapper.cancel();
                boolean isPassedException = isPassedException(e);
                if (!isPassedException) {
                    logger.error("Write to output stream error.", e);
                }

                onCompleteCallback(CFSStatus.IO_ERROR);
            }
        }

        @Override
        public void onError(Throwable t) {
            if (Status.fromThrowable(t) == Status.CANCELLED || StatusHelper.isCancelled(t)) {
                onCompleteCallback(CFSStatus.CANCELED);
                return;
            }
            logger.debug("Download file error.", t);
            onCompleteCallback(CFSStatus.UNKNOWN);
        }

        @Override
        public void onCompleted() {
            logger.debug("Download file complete. all request count: {}", receiveCount.get() - 1);
            onCompleteCallback(CFSStatus.SUCCESS);
        }

        private void onCompleteCallback(CFSStatus status) {
            if (callback == null) {
                return;
            }
            callback.onComplete(status);
        }

        private void onFileNotExists() {

            // TODO: file not exists, choose other server
        }

        private void writeTo(List<DownloadBlockData> downloadBlockData,
                             OutputStream stream,
                             long validBytes) throws IOException {
            if (validBytes < 0) {
                writeFully(downloadBlockData, stream);
                return;
            }

            final int size = downloadBlockData.size() - 1;
            int index = 0;
            for (DownloadBlockData downloadBlockDatum : downloadBlockData) {
                byte[] data = downloadBlockDatum.getData().toByteArray();
                long len = index == size ? validBytes : -1;
                writeOffset(stream, data, len);
                index++;
            }
            stream.flush();
        }

        private void writeFully(List<DownloadBlockData> downloadBlockData,
                                OutputStream stream) throws IOException {
            for (DownloadBlockData downloadBlockDatum : downloadBlockData) {
                byte[] data = downloadBlockDatum.getData().toByteArray();
                writeOffset(stream, data, -1);
            }
            stream.flush();
        }

        private void writeOffset(OutputStream stream, byte[] data, long len) throws IOException {
            if (len < 0) {
                stream.write(data);
                return;
            }
            stream.write(data, 0, (int) len);
        }
    }
}
