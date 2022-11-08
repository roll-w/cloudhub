package org.huel.cloudhub.file.server.service.file;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.apache.commons.lang3.RandomStringUtils;
import org.huel.cloudhub.file.fs.LocalFileServer;
import org.huel.cloudhub.file.fs.LockException;
import org.huel.cloudhub.file.fs.ServerFile;
import org.huel.cloudhub.file.fs.block.Block;
import org.huel.cloudhub.file.fs.container.ContainerAllocator;
import org.huel.cloudhub.file.fs.container.ContainerProperties;
import org.huel.cloudhub.file.fs.container.ContainerWriterOpener;
import org.huel.cloudhub.file.fs.container.file.ContainerFileWriter;
import org.huel.cloudhub.file.fs.container.file.FileWriteStrategy;
import org.huel.cloudhub.file.fs.meta.MetaException;
import org.huel.cloudhub.file.rpc.block.*;
import org.huel.cloudhub.server.StreamObserverWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author RollW
 */
@Service
public class BlockReceiveService extends BlockUploadServiceGrpc.BlockUploadServiceImplBase {
    private final Logger logger = LoggerFactory.getLogger(BlockReceiveService.class);
    private final ContainerAllocator containerAllocator;
    private final ContainerProperties containerProperties;
    private final ContainerWriterOpener containerWriterOpener;
    private final LocalFileServer localFileServer;

    private static final int BUFFERED_BLOCK_SIZE = 320;

    public BlockReceiveService(ContainerAllocator containerAllocator,
                               ContainerProperties containerProperties,
                               ContainerWriterOpener containerWriterOpener,
                               LocalFileServer localFileServer) throws IOException {
        this.containerAllocator = containerAllocator;
        this.containerProperties = containerProperties;
        this.containerWriterOpener = containerWriterOpener;
        this.localFileServer = localFileServer;
        initStagingDirectory();
    }

    private void initStagingDirectory() throws IOException {
        ServerFile file = localFileServer.getServerFileProvider()
                .openFile(containerProperties.getStagePath());
        file.mkdirs();
    }

    private ServerFile openNewStagingFile() throws IOException {
        ServerFile file = localFileServer.getServerFileProvider().openFile(
                containerProperties.getStagePath(),
                RandomStringUtils.randomAlphanumeric(20));
        if (file.exists()) {
            return openNewStagingFile();
        }
        file.createFile();
        return file;
    }

    @Override
    public StreamObserver<UploadBlocksRequest> uploadBlocks(StreamObserver<UploadBlocksResponse> responseObserver) {
        try {
            return new UploadBlocksRequestObserver(responseObserver);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private class UploadBlocksRequestObserver implements StreamObserver<UploadBlocksRequest> {
        private final StreamObserverWrapper<UploadBlocksResponse> responseObserver;
        private final ServerFile stagingFile;
        private final AtomicInteger received = new AtomicInteger(0);
        private OutputStream stagingOut;

        // save to staging dir, and puts blocks in the area.
        // when receive all requests,
        // reads all data into the container step by step after.

        private String fileId;
        private int indexCount = -1;
        private long validBytes;
        private long fileLength;
        private String checkValue;

        UploadBlocksRequestObserver(StreamObserver<UploadBlocksResponse> responseObserver) throws IOException {
            this.responseObserver = new StreamObserverWrapper<>(responseObserver);
            this.stagingFile = openNewStagingFile();
        }

        private void checkExistsWithClose(String fileId) {
            boolean exists = containerAllocator.dataExists(fileId, ContainerAllocator.LOCAL);
            UploadBlocksResponse response = UploadBlocksResponse.newBuilder()
                    .setFileExists(exists)
                    .build();
            responseObserver.onNext(response);
            if (!exists) {
                return;
            }
            logger.debug("File exists, id={}", fileId);
            responseObserver.onCompleted();
        }

        private void saveCheckMessageInfo(UploadBlocksRequest.CheckMessage checkMessage) {
            indexCount = checkMessage.getRequestCount();
            validBytes = checkMessage.getValidBytes();
            fileLength = checkMessage.getFileLength();
            checkValue = checkMessage.getCheckValue();
            logger.debug("Receive upload message. count={};validBytes={};fileLen={};check={}",
                    indexCount, validBytes, fileLength, checkValue);
            try {
                stagingOut = stagingFile.openOutput();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void onNext(UploadBlocksRequest request) {
            if (responseObserver.isClose()) {
                return;
            }
            if (request.getUploadCase() == UploadBlocksRequest.UploadCase.UPLOAD_NOT_SET) {
                responseObserver.onError(Status.INVALID_ARGUMENT.asException());
                return;
            }
            if (fileId != null && !fileId.equals(request.getIdentity())) {
                Exception exception =
                        new IllegalStateException("The ids of the files before and after are different.");
                responseObserver.onError(exception);
                logger.error("error receive blocks: {}.", exception.getMessage());
                return;
            }
            fileId = request.getIdentity();
            checkExistsWithClose(fileId);

            if (request.getUploadCase() == UploadBlocksRequest.UploadCase.CHECK_MESSAGE) {
                saveCheckMessageInfo(request.getCheckMessage());
                return;
            }

            UploadBlocksInfo uploadBlocksInfo = request.getUploadBlocks();
            final int count = uploadBlocksInfo.getBlocksCount();
            logger.debug("receive blocks. index={};count=[{}];id={};",
                    uploadBlocksInfo.getIndex(), count, request.getIdentity());
            received.incrementAndGet();
            long valid = uploadBlocksInfo.getIndex() == count ? validBytes : -1;
            try {
                writeOut(uploadBlocksInfo, valid);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        private void writeOut(UploadBlocksInfo uploadBlocksInfo, long validBytes) throws IOException {
            if (stagingOut == null) {
                return;
            }
            int i = 0;
            for (UploadBlockData uploadBlockData : uploadBlocksInfo.getBlocksList()) {
                if (i == uploadBlocksInfo.getBlocksCount() - 1 && validBytes >= 0) {
                    stagingOut.write(uploadBlockData.getData().toByteArray(), 0,
                            (int) validBytes);
                    return;
                }
                stagingOut.write(uploadBlockData.getData().toByteArray());
                i++;
            }
        }

        @Override
        public void onError(Throwable t) {
            logger.error("receive blocks error.", t);
        }

        private void closeAndExit() {
            try {
                stagingOut.close();
                delete();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private long closeAndCalcLength() {
            try {
                stagingOut.close();
                return stagingFile.length();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return -1;
        }

        private void delete() {
            try {
                stagingFile.delete();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onCompleted() {
            if (indexCount <= 0 && responseObserver.isOpen()) {
                closeAndExit();
                logger.error("Invalid index count {} in uploading {}", indexCount, fileId);
                responseObserver.onError(Status.INVALID_ARGUMENT.asException());
                return;
            }
            if (indexCount != received.get() && responseObserver.isOpen()) {
                closeAndExit();
                logger.debug("Data may have been lost in uploading {}. given: {}, receive: {}",
                        fileId, indexCount, received.get());
                responseObserver.onError(Status.DATA_LOSS.asException());
                return;
            }
            if (responseObserver.isClose()) {
                closeAndExit();
                return;
            }
            long savedLength = closeAndCalcLength();
            if (savedLength != fileLength) {
                delete();
                logger.debug("Data may have been lost in uploading {}. given len: {}, receive len: {}",
                        fileId, fileLength, savedLength);
                responseObserver.onError(Status.DATA_LOSS.asException());
                return;
            }

            if (savedLength < 0) {
                delete();
                logger.debug("Internal receive data error, file length < 0.");
                responseObserver.onError(Status.INTERNAL.asException());
                return;
            }

            try (ContainerFileWriter containerFileWriter = new ContainerFileWriter(fileId,
                    savedLength, ContainerAllocator.LOCAL, containerAllocator, containerWriterOpener, FileWriteStrategy.SEQUENCE)) {
                writeUntilEnd(containerFileWriter, stagingFile.openInput(), BUFFERED_BLOCK_SIZE, validBytes);
            } catch (IOException | MetaException e) {
                logger.error("Occurred error here while saving to container.", e);
            } catch (LockException e) {
                logger.error("Cannot get container's lock.", e);
            }
            responseObserver.onNext(UploadBlocksResponse.newBuilder().build());
            responseObserver.onCompleted();
            delete();
        }
    }

    private void writeUntilEnd(ContainerFileWriter writer,
                               InputStream inputStream,
                               int blockSize, long validBytes) throws IOException, LockException, MetaException {
        List<Block> blocks = new ArrayList<>();
        Block block;
        int size = 0;
        while ((block = readBlock(inputStream, validBytes)) != null) {
            if (size == blockSize) {
                writer.writeBlocks(blocks);
                blocks = new ArrayList<>();
                size = 0;
            }
            blocks.add(block);
            size++;
        }
        writer.writeBlocks(blocks);
        inputStream.close();
    }

    private Block readBlock(InputStream inputStream, long validBytes) throws IOException {
        int size = containerProperties.getBlockSizeInBytes();
        byte[] chunk = new byte[size];
        int read = inputStream.read(chunk);
        if (read == -1) {
            return null;
        }
        if (read == 0) {
            return new Block(chunk, validBytes);
        }
        return new Block(chunk, read);
    }

}
