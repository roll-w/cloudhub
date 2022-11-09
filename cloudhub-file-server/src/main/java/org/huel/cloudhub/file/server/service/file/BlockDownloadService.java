package org.huel.cloudhub.file.server.service.file;

import com.google.protobuf.ByteString;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.huel.cloudhub.file.fs.LockException;
import org.huel.cloudhub.file.fs.block.ContainerBlock;
import org.huel.cloudhub.file.fs.block.FileBlockMetaInfo;
import org.huel.cloudhub.file.fs.container.ContainerAllocator;
import org.huel.cloudhub.file.fs.container.ContainerGroup;
import org.huel.cloudhub.file.fs.container.ContainerProperties;
import org.huel.cloudhub.file.fs.container.ContainerReadOpener;
import org.huel.cloudhub.file.fs.container.file.ContainerFileReader;
import org.huel.cloudhub.file.rpc.block.*;
import org.huel.cloudhub.file.server.service.id.ServerIdService;
import org.huel.cloudhub.server.GrpcProperties;
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
    private final ContainerAllocator containerAllocator;
    private final ContainerProperties containerProperties;
    private final GrpcProperties grpcProperties;
    private final ServerIdService serverIdService;

    public BlockDownloadService(ContainerReadOpener containerReadOpener,
                                ContainerAllocator containerAllocator,
                                ContainerProperties containerProperties,
                                GrpcProperties grpcProperties,
                                ServerIdService serverIdService) {
        this.containerReadOpener = containerReadOpener;
        this.containerAllocator = containerAllocator;
        this.containerProperties = containerProperties;
        this.grpcProperties = grpcProperties;
        this.serverIdService = serverIdService;
    }

    @Override
    public void downloadBlocks(DownloadBlockRequest request,
                               StreamObserver<DownloadBlockResponse> responseObserver) {
        final String fileId = request.getFileId();
        final String source = getId(request);
        ContainerGroup containerGroup =
                containerAllocator.findContainerGroupByFile(fileId, source);
        FileBlockMetaInfo fileBlockMetaInfo = containerGroup.getFileBlockMetaInfo(fileId);
        if (fileBlockMetaInfo == null) {
            responseObserver.onError(Status.NOT_FOUND.asException());
            responseObserver.onCompleted();
            return;
        }

        final long fileLength = fileBlockMetaInfo.getFileLength();
        final long responseSize = grpcProperties.getMaxRequestSizeBytes() >> 1;
        final int responseCount = Maths.ceilDivideReturnsInt(fileLength, responseSize);
        final int maxBlocksInResponse = (int) (responseSize / containerProperties.getBlockSizeInBytes());

        DownloadBlockResponse firstResponse = buildFirstResponse(fileBlockMetaInfo,
                responseCount,
                "0");
        responseObserver.onNext(firstResponse);

        readSendResponse(fileId, containerGroup, fileBlockMetaInfo,
                responseObserver, maxBlocksInResponse, 0);

    }

    // TODO: set by meta-server.
    private static final int RETRY_TIMES = 5;

    private void readSendResponse(String fileId, ContainerGroup containerGroup,
                                  FileBlockMetaInfo fileBlockMetaInfo,
                                  StreamObserver<DownloadBlockResponse> responseObserver,
                                  int maxBlocksInResponse, int retry) {
        if (retry >= RETRY_TIMES) {
            logger.error("Send failed because the number of retry times has reached the upper limit.");
            responseObserver.onError(Status.UNAVAILABLE.asException());
            return;
        }
        if (retry != 0) {
            logger.debug("Retry send download response for file '{}'.", fileId);
        }
        try (ContainerFileReader containerFileReader = new ContainerFileReader(
                containerReadOpener, containerAllocator, fileId, containerGroup, fileBlockMetaInfo)) {
            sendUtilEnd(responseObserver, containerFileReader, maxBlocksInResponse);
            responseObserver.onCompleted();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (LockException e) {
            logger.error("Cannot get container's read lock.", e);
            readSendResponse(fileId, containerGroup, fileBlockMetaInfo,
                    responseObserver, maxBlocksInResponse, retry + 1);
        }
    }

    private void sendUtilEnd(StreamObserver<DownloadBlockResponse> responseObserver,
                             ContainerFileReader fileReader, int readSize) throws IOException, LockException {
        int index = 0;
        while (fileReader.hasNext()) {
            List<ContainerBlock> read = fileReader.read(readSize);
            if (read == null) {
                return;
            }
            DownloadBlockResponse response = buildBlockDataResponse(read, index);
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

    private DownloadBlockResponse buildBlockDataResponse(List<ContainerBlock> containerBlocks, int index) {
        List<DownloadBlockData> downloadBlockData = new ArrayList<>();
        containerBlocks.forEach(containerBlock -> {
            downloadBlockData.add(DownloadBlockData.newBuilder()
                    .setData(ByteString.copyFrom(
                            containerBlock.getData(), 0,
                            (int) containerBlock.getValidBytes())
                    )
                    .build());
            containerBlock.release();
        });
        DownloadBlocksInfo downloadBlocksInfo = DownloadBlocksInfo.newBuilder()
                .addAllData(downloadBlockData)
                .setIndex(index)
                .build();
        return DownloadBlockResponse.newBuilder()
                .setDownloadBlocks(downloadBlocksInfo)
                .build();
    }


    private String getId(DownloadBlockRequest request) {
        if (!request.hasSourceId()) {
            return ContainerAllocator.LOCAL;
        }
        final String id = request.getSourceId();
        if (id.equals(serverIdService.getServerId())) {
            return ContainerAllocator.LOCAL;
        }
        return id;
    }

}
