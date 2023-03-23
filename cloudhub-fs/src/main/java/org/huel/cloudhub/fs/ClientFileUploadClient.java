package org.huel.cloudhub.fs;

import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import org.apache.commons.lang3.RandomStringUtils;
import org.huel.cloudhub.client.rpc.file.ClientFileUploadData;
import org.huel.cloudhub.client.rpc.file.ClientFileUploadDataSegment;
import org.huel.cloudhub.client.rpc.file.ClientFileUploadRequest;
import org.huel.cloudhub.client.rpc.file.ClientFileUploadResponse;
import org.huel.cloudhub.client.rpc.file.ClientFileUploadServiceGrpc;
import org.huel.cloudhub.file.io.BufferedStreamIterator;
import org.huel.cloudhub.file.io.ReopenableInputStream;
import org.huel.cloudhub.rpc.GrpcProperties;
import org.huel.cloudhub.rpc.StreamObserverWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author RollW
 */
@SuppressWarnings({"UnstableApiUsage"})
public class ClientFileUploadClient {
    private static final Logger logger = LoggerFactory.getLogger(ClientFileUploadClient.class);

    private final GrpcProperties grpcProperties;
    private final ClientFileUploadServiceGrpc.ClientFileUploadServiceStub stub;

    public ClientFileUploadClient(MetaServerConnection metaServerConnection,
                                  GrpcProperties grpcProperties) {
        this.grpcProperties = grpcProperties;
        this.stub = ClientFileUploadServiceGrpc.newStub(
                metaServerConnection.getManagedChannel());
    }

    public void uploadFile(InputStream inputStream,
                           String tempDir,
                           ClientFileUploadCallback callback) throws IOException {
        logger.debug("Start calculation on the given input stream.");
        // TODO:
        Hasher sha256 = Hashing.sha256().newHasher();
        ReopenableInputStream reopenableInputStream =
                convertInputStream(tempDir, inputStream, sha256);
        final String hash = reopenableInputStream.getHash(sha256).toString();
        logger.trace("Start upload file with hash={}", hash);
        int bufferSize = (int) (grpcProperties.getMaxRequestSizeBytes() >> 1) / 10;

        ClientFileUploadResponseObserver observer = new ClientFileUploadResponseObserver(
                callback, hash, reopenableInputStream.getLength(),
                reopenableInputStream, bufferSize, 10);
        StreamObserver<ClientFileUploadRequest> requestStreamObserver
                = stub.uploadFile(observer);
        observer.setRequestStreamObserver(requestStreamObserver);
        ClientFileUploadRequest.CheckMessage checkMessage =
                ClientFileUploadRequest.CheckMessage
                        .newBuilder()
                        .setFileHash(hash)
                        .setSize(reopenableInputStream.getLength())
                        .build();
        requestStreamObserver.onNext(ClientFileUploadRequest
                .newBuilder()
                .setCheckMessage(checkMessage)
                .build()
        );
    }

    private static class ClientFileUploadResponseObserver implements StreamObserver<ClientFileUploadResponse> {
        private StreamObserverWrapper<ClientFileUploadRequest> requestStreamObserver;
        private final ClientFileUploadCallback callback;
        private final String fileId;
        private final long fileSize;
        private boolean called = false;
        private final BufferedStreamIterator iterator;
        private final int maxUploadBlockCount;

        private ClientFileUploadResponseObserver(ClientFileUploadCallback callback, String fileId, long fileSize, InputStream stream,
                                                 int bufferSize, int maxUploadBlockCount) {
            this.callback = callback;
            this.fileId = fileId;
            this.fileSize = fileSize;
            this.iterator = new BufferedStreamIterator(stream, bufferSize);
            this.maxUploadBlockCount = maxUploadBlockCount;
        }

        public void setRequestStreamObserver(StreamObserver<ClientFileUploadRequest> requestStreamObserver) {
            this.requestStreamObserver = new StreamObserverWrapper<>(requestStreamObserver);
        }

        @Override
        public void onNext(ClientFileUploadResponse value) {
            logger.debug("Recv response={}", value);
            if (value.getResponseCase() == ClientFileUploadResponse.ResponseCase.RESPONSE_NOT_SET) {
                return;
            }
            if (value.getResponseCase() == ClientFileUploadResponse.ResponseCase.SUCCESS) {
                logger.debug("Upload file success.");
                if (value.getSuccess()) {
                    callSuccess();
                }
                return;
            }
            if (value.getExist()) {
                callSuccess();
                logger.debug("File exists.");
            }

            while (true) {
                try {
                    if (!sendData()) break;
                } catch (IOException e) {
                    logger.debug("Send error", e);
                    break;
                }
            }
            logger.debug("Upload complete");
            try {
                iterator.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        private boolean sendData() throws IOException {
            if (requestStreamObserver.isClose()) {
                return false;
            }

            List<ClientFileUploadData> blocks = new ArrayList<>();
            boolean isLast = false;
            for (int i = 0; i < maxUploadBlockCount; i++) {
                BufferedStreamIterator.Buffer buffer =
                        iterator.nextBuffer();
                ClientFileUploadData uploadBlockData = ClientFileUploadData.newBuilder()
                        .setData(ByteString.copyFrom(
                                buffer.getData(), 0, (int) buffer.getSize()))
                        .build();
                blocks.add(uploadBlockData);
                if (buffer.notFilled()) {
                    isLast = true;
                    break;
                }
            }

            ClientFileUploadDataSegment segment = ClientFileUploadDataSegment.newBuilder()
                    .addAllData(blocks)
                    .build();
            ClientFileUploadRequest request = ClientFileUploadRequest.newBuilder()
                    .setDataSegment(segment)
                    .build();
            requestStreamObserver.onNext(request);
            if (isLast) {
                requestStreamObserver.onCompleted();
                return false;
            }
            return true;
        }

        @Override
        public void onError(Throwable t) {
            if (callback != null && !called) {
                called = true;
                callback.onComplete(false, null, -1);
            }
            logger.error("On error: ", t);
        }

        @Override
        public void onCompleted() {
            callSuccess();
            logger.debug("Upload complete.");
        }

        private void callSuccess() {
            if (callback != null && !called) {
                called = true;
                callback.onComplete(true, fileId, fileSize);
            }
        }
    }

    private static ReopenableInputStream convertInputStream(String tempDir,
                                                            InputStream inputStream,
                                                            Hasher... hashers)
            throws IOException {
        File tempDirFile = new File(tempDir);
        if (!tempDirFile.exists()) {
            tempDirFile.mkdirs();
        }
        File tempFile = new File(tempDirFile,
                RandomStringUtils.randomAlphanumeric(20));
        return new ReopenableInputStream(inputStream, tempFile, hashers);
    }
}
