package org.huel.cloudhub.meta.server.service.file;

import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import io.grpc.Context;
import io.grpc.stub.StreamObserver;
import org.apache.commons.lang3.RandomStringUtils;
import org.huel.cloudhub.client.rpc.file.*;
import org.huel.cloudhub.file.io.ReopenableInputStream;
import org.huel.cloudhub.meta.fs.FileObjectUploadStatus;
import org.huel.cloudhub.meta.server.configuration.FileProperties;
import org.huel.cloudhub.rpc.GrpcProperties;
import org.huel.cloudhub.rpc.StreamObserverWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;

/**
 * @author RollW
 */
@Service
public class ClientFileUploadDispatchService extends ClientFileUploadServiceGrpc.ClientFileUploadServiceImplBase {
    private final FileUploadService fileUploadService;
    private final GrpcProperties grpcProperties;
    private final FileProperties fileProperties;
    private final Logger logger = LoggerFactory.getLogger(ClientFileUploadDispatchService.class);

    public ClientFileUploadDispatchService(FileUploadService fileUploadService,
                                           GrpcProperties grpcProperties, FileProperties fileProperties) {
        this.fileUploadService = fileUploadService;
        this.grpcProperties = grpcProperties;
        this.fileProperties = fileProperties;
    }

    @Override
    public StreamObserver<ClientFileUploadRequest> uploadFile(StreamObserver<ClientFileUploadResponse> responseObserver) {
        return new ClientFileUploadRequestObserver(responseObserver);
    }

    private class ClientFileUploadRequestObserver implements StreamObserver<ClientFileUploadRequest> {
        private final StreamObserverWrapper<ClientFileUploadResponse> responseObserver;
        private final File tempFile;
        private final BufferedOutputStream fileOutputStream;
        private final Context context;
        private String hash;

        private ClientFileUploadRequestObserver(StreamObserver<ClientFileUploadResponse> responseObserver) {
            this.responseObserver = new StreamObserverWrapper<>(responseObserver);
            this.tempFile = getTempFile();
            try {
                tempFile.createNewFile();
                this.fileOutputStream = new BufferedOutputStream(new FileOutputStream(tempFile, false));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            this.context = Context.current().fork();
        }

        @Override
        public void onNext(ClientFileUploadRequest value) {
            if (value.getUploadCase() == ClientFileUploadRequest.UploadCase.UPLOAD_NOT_SET) {
                return;
            }
            if (value.getUploadCase() == ClientFileUploadRequest.UploadCase.CHECK_MESSAGE) {
                ClientFileUploadRequest.CheckMessage checkMessage = value.getCheckMessage();
                hash = checkMessage.getFileHash();
                if (fileUploadService.checkFileExists(checkMessage.getFileHash())) {
                    responseObserver.onNext(ClientFileUploadResponse.newBuilder()
                            .setExist(true)
                            .build());
                    return;
                }
                responseObserver.onNext(ClientFileUploadResponse.newBuilder()
                        .setExist(false)
                        .build());
                return;
            }
            logger.debug("Recv data");
            ClientFileUploadDataSegment segment = value.getDataSegment();
            writeTo(segment);
        }

        private void writeTo(ClientFileUploadDataSegment segment) {
            try {
                for (ClientFileUploadData clientFileUploadData : segment.getDataList()) {
                    logger.debug("write: size={}", clientFileUploadData.getData().toByteArray().length);
                    fileOutputStream.write(clientFileUploadData.getData().toByteArray());
                    fileOutputStream.flush();
                }
                logger.debug("write data.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(Throwable t) {
            logger.debug("Dispatch error.", t);
        }

        private void sendData() {
            try {
                Hasher sha256 = Hashing.sha256().newHasher();
                ReopenableInputStream inputStream = new ReopenableInputStream(
                        new FileInputStream(tempFile), tempFile, true, sha256);
                String hash = sha256.hash().toString();
                long len = inputStream.getLength();
                fileUploadService.uploadFile(inputStream, hash, len,
                        new Callback(responseObserver));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }


        @Override
        public void onCompleted() {
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            context.run(this::sendData);
        }
    }

    private class Callback implements FileUploadStatusCallback  {
        private final StreamObserverWrapper<ClientFileUploadResponse> responseObserver;

        private Callback(StreamObserverWrapper<ClientFileUploadResponse> responseObserver) {
            this.responseObserver = responseObserver;
        }
        @Override
        public void onNextStatus(FileObjectUploadStatus status) {
            logger.debug("Next status: {}.", status);

            if (!status.isLastStatus()) {
                return;
            }
            if (status.isAvailable()) {
                responseObserver.onNext(ClientFileUploadResponse.newBuilder()
                        .setSuccess(true).build());
                responseObserver.onCompleted();
                return;
            }
            responseObserver.onNext(ClientFileUploadResponse.newBuilder()
                    .setSuccess(false).build());
            responseObserver.onCompleted();
        }
    }

    private File getTempFile() {
        File tempDir = new File(fileProperties.getTempFilePath());
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }
        return new File(tempDir,
                RandomStringUtils.randomAlphanumeric(20));
    }
}
