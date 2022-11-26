package org.huel.cloudhub.file.server.service.file;

import com.google.protobuf.ByteString;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.huel.cloudhub.file.fs.LockException;
import org.huel.cloudhub.file.fs.block.ContainerBlock;
import org.huel.cloudhub.file.fs.block.FileBlockMetaInfo;
import org.huel.cloudhub.file.fs.container.ContainerFinder;
import org.huel.cloudhub.file.fs.container.ContainerGroup;
import org.huel.cloudhub.file.fs.container.ContainerProperties;
import org.huel.cloudhub.file.fs.container.ContainerReadOpener;
import org.huel.cloudhub.file.fs.container.file.ContainerFileReader;
import org.huel.cloudhub.file.rpc.block.*;
import org.huel.cloudhub.file.server.service.SourceServerGetter;
import org.huel.cloudhub.rpc.GrpcProperties;
import org.huel.cloudhub.util.math.Maths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author RollW
 */
@Service
public class BlockDownloadService extends BlockDownloadServiceGrpc.BlockDownloadServiceImplBase {
    private final Logger logger = LoggerFactory.getLogger(BlockDownloadService.class);
    private final ContainerReadOpener containerReadOpener;
    private final ContainerFinder containerFinder;
    private final ContainerProperties containerProperties;
    private final GrpcProperties grpcProperties;
    private final SourceServerGetter.ServerInfo serverInfo;

    public BlockDownloadService(ContainerReadOpener containerReadOpener,
                                ContainerFinder containerFinder,
                                ContainerProperties containerProperties,
                                GrpcProperties grpcProperties,
                                SourceServerGetter sourceServerGetter) {
        this.containerReadOpener = containerReadOpener;
        this.containerFinder = containerFinder;
        this.containerProperties = containerProperties;
        this.grpcProperties = grpcProperties;
        this.serverInfo = sourceServerGetter.getLocalServer();
    }

    @Override
    public void downloadBlocks(DownloadBlockRequest request,
                               StreamObserver<DownloadBlockResponse> responseObserver) {
        final String fileId = request.getFileId();
        final String source = getId(request);
        ContainerGroup containerGroup =
                containerFinder.findContainerGroupByFile(fileId, source);
        if (containerGroup == null) {
            responseFileNotExists(responseObserver);
            return;
        }
        FileBlockMetaInfo fileBlockMetaInfo = containerGroup.getFileBlockMetaInfo(fileId);
        if (fileBlockMetaInfo == null) {
            responseFileNotExists(responseObserver);
            return;
        }
        final long fileLen = fileBlockMetaInfo.getFileLength();
        BytesInfo bytesInfo = readInfoFromRequest(request, containerGroup.getBlockSizeInBytes(), fileLen);

        final long length = bytesInfo == null
                ? fileLen : bytesInfo.length();

        final long responseSize = grpcProperties.getMaxRequestSizeBytes() >> 1;
        final int responseCount = Maths.ceilDivideReturnsInt(length, responseSize);
        final int maxBlocksInResponse = (int) (responseSize / containerProperties.getBlockSizeInBytes());

        DownloadBlockResponse firstResponse = buildFirstResponse(fileBlockMetaInfo,
                responseCount,
                "0");
        responseObserver.onNext(firstResponse);

        readSendResponse(fileId, containerGroup, fileBlockMetaInfo,
                responseObserver, bytesInfo, maxBlocksInResponse, responseCount, 0);

    }

    private record BytesInfo(
            long start, long end
    ) {
        long length() {
            return end - start + 1;
        }
    }

    private BytesInfo readInfoFromRequest(DownloadBlockRequest request, long blockSizeInBytes, long fileLength) {
        if (!request.hasSegmentInfo()) {
            return null;
        }
        DownloadBlocksSegmentInfo segmentInfo = request.getSegmentInfo();
        if (segmentInfo.getSegmentCase() == DownloadBlocksSegmentInfo.SegmentCase.BLOCKS) {
            int startIndex = segmentInfo.getBlocks().getStartIndex();
            int endIndex = segmentInfo.getBlocks().getEndIndex();
            if (startIndex < 0 || endIndex < 0) {
                return null;
            }

            long start = blockSizeInBytes * segmentInfo.getBlocks().getStartIndex();
            long end = blockSizeInBytes * segmentInfo.getBlocks().getEndIndex();
            return new BytesInfo(start, end);
        }
        long start = segmentInfo.getBytes().getStartBytes();
        long end = segmentInfo.getBytes().getEndBytes();
        if (start < 0) {
            start = 0;
        }
        if (end < 0) {
            end = fileLength - 1;
        }
        if (start > end) {
            return new BytesInfo(end, start);
        }
        return new BytesInfo(start, end);
    }

    private void responseFileNotExists(StreamObserver<DownloadBlockResponse> responseObserver) {
        responseObserver.onNext(DownloadBlockResponse.newBuilder()
                .setFileExists(false)
                .build());
        responseObserver.onCompleted();
    }

    // TODO: set by meta-server.
    private static final int RETRY_TIMES = 3;

    private void readSendResponse(String fileId, ContainerGroup containerGroup,
                                  FileBlockMetaInfo fileBlockMetaInfo,
                                  StreamObserver<DownloadBlockResponse> responseObserver,
                                  BytesInfo bytesInfo,
                                  int maxBlocksInResponse, int responseCount, int retry) {
        if (retry >= RETRY_TIMES) {
            logger.error("Send failed because the number of retry times has reached the upper limit.");
            responseObserver.onError(Status.UNAVAILABLE.asException());
            return;
        }
        if (retry != 0) {
            logger.debug("Retry send download response for file '{}'.", fileId);
        }
        try (ContainerFileReader containerFileReader = new ContainerFileReader(
                containerReadOpener, containerFinder, fileId, containerGroup, fileBlockMetaInfo)) {
            sendUtilEnd(bytesInfo, responseObserver, containerFileReader, maxBlocksInResponse, responseCount);
            responseObserver.onCompleted();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (LockException e) {
            logger.error("Cannot get container's read lock.", e);
            readSendResponse(fileId, containerGroup, fileBlockMetaInfo,
                    responseObserver, bytesInfo, maxBlocksInResponse, responseCount, retry + 1);
        }
    }

    private void sendUtilEnd(BytesInfo bytesInfo, StreamObserver<DownloadBlockResponse> responseObserver,
                             ContainerFileReader fileReader, int readSize, int responseCount) throws IOException, LockException {
        if (bytesInfo == null) {
            sendFullFile(responseObserver, fileReader, readSize);
            return;
        }
        long blockSize = fileReader.getBlockSizeInBytes();
        int startBlock = (int) Math.floorDiv(bytesInfo.start(), blockSize);
        int endBlock = (int) Math.floorDiv(bytesInfo.end(), blockSize);
        long startOff = bytesInfo.start() - startBlock * blockSize;
        long endLen = bytesInfo.end() - endBlock * blockSize;
        fileReader.seek(startBlock);
        int index = 1;
        while (fileReader.hasNext()) {
            List<ContainerBlock> read = fileReader.read(readSize);
            if (read == null) {
                return;
            }
            if (index == responseCount) {
                DownloadBlockResponse response = buildBlockDataResponse(
                        read, index,
                        index == 1 ? startOff : 0,
                        endLen);
                logger.debug("Send download response in the end. block size = {}", read.size());
                responseObserver.onNext(response);
                return;
            }
            if (index == 1) {
                DownloadBlockResponse response =
                        buildBlockDataResponse(read, index, startOff, -1);
                logger.debug("Send download response in first. block size = {}", read.size());
                responseObserver.onNext(response);
                index++;
                continue;
            }
            DownloadBlockResponse response = buildBlockDataResponse(read, index, -1, -1);
            logger.debug("Send download response. block size = {}", read.size());
            responseObserver.onNext(response);
            index++;
        }
    }

    private void sendFullFile(StreamObserver<DownloadBlockResponse> responseObserver,
                              ContainerFileReader fileReader, int readSize) throws LockException, IOException {
        int index = 0;
        while (fileReader.hasNext()) {
            List<ContainerBlock> read = fileReader.read(readSize);
            if (read == null) {
                return;
            }
            DownloadBlockResponse response = buildBlockDataResponse(read, index, -1, -1);
            logger.debug("Send download response. block size ={}", read.size());
            responseObserver.onNext(response);
            index++;
        }
    }

    private DownloadBlockResponse buildFirstResponse(FileBlockMetaInfo fileBlockMetaInfo,
                                                     int responseCount, String checkValue) {
        DownloadBlockResponse.CheckMessage checkMessage = DownloadBlockResponse.CheckMessage
                .newBuilder()
                .setFileLength(fileBlockMetaInfo.getFileLength())
                .setResponseCount(responseCount)
                .setValidBytes(fileBlockMetaInfo.getValidBytes())
                .setCheckValue(checkValue)
                .build();
        return DownloadBlockResponse.newBuilder()
                .setCheckMessage(checkMessage)
                .build();
    }

    private DownloadBlockResponse buildBlockDataResponse(List<ContainerBlock> containerBlocks, int index, long startOff, long endLen) {
        List<DownloadBlockData> downloadBlockData = new ArrayList<>();
        final int size = containerBlocks.size() - 1;
        int i = 0;
        for (ContainerBlock containerBlock : containerBlocks) {
            if (i == 0 && i == size) {
                downloadBlockData.add(buildFirstWithLenBlockData(
                        containerBlock, (int) startOff, (int) endLen));
                break;
            }

            if (i == 0) {
                downloadBlockData.add(buildFirstBlockData(containerBlock, (int) startOff));
                i++;
                continue;
            }
            if (i == size) {
                downloadBlockData.add(buildLastBlockData(containerBlock, (int) endLen));
                i++;
                continue;
            }
            i++;
            downloadBlockData.add(buildCommonBlockData(containerBlock));
        }
        DownloadBlocksInfo downloadBlocksInfo = DownloadBlocksInfo.newBuilder()
                .addAllData(downloadBlockData)
                .setIndex(index)
                .build();
        return DownloadBlockResponse.newBuilder()
                .setDownloadBlocks(downloadBlocksInfo)
                .build();
    }

    private DownloadBlockData buildFirstWithLenBlockData(ContainerBlock block, int startOff, int endLen) {
        if (startOff <= 0) {
            return buildCommonBlockData(block);
        }
        ByteString byteString = ByteString.copyFrom(
                block.getData(), startOff,
                endLen - startOff + 1
        );
        block.release();
        return DownloadBlockData.newBuilder()
                .setData(byteString)
                .build();
    }

    private DownloadBlockData buildFirstBlockData(ContainerBlock block, int startOff) {
        if (startOff <= 0) {
            return buildCommonBlockData(block);
        }
        ByteString byteString = ByteString.copyFrom(
                block.getData(), startOff,
                ((int) block.getValidBytes() - startOff)
        );
        block.release();
        return DownloadBlockData.newBuilder()
                .setData(byteString)
                .build();
    }

    private DownloadBlockData buildLastBlockData(ContainerBlock block, int endLen) {
        if (endLen <= 0) {
            return buildCommonBlockData(block);
        }
        ByteString byteString = ByteString.copyFrom(
                block.getData(), 0,
                endLen);
        block.release();
        return DownloadBlockData.newBuilder()
                .setData(byteString)
                .build();
    }

    private DownloadBlockData buildCommonBlockData(ContainerBlock block) {
        try {
            return DownloadBlockData.newBuilder()
                    .setData(ByteString.copyFrom(block.getData(),
                            0,
                            (int) block.getValidBytes()))
                    .build();
        } finally {
            block.release();
        }
    }


    private String getId(DownloadBlockRequest request) {
        if (!request.hasSourceId()) {
            return ContainerFinder.LOCAL;
        }
        final String id = request.getSourceId();
        if (id.equals(serverInfo.id())) {
            return ContainerFinder.LOCAL;
        }
        return id;
    }

}
