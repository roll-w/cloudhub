package org.huel.cloudhub.file.server.file;

import com.google.protobuf.ByteString;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.huel.cloudhub.file.fs.block.ContainerBlock;
import org.huel.cloudhub.file.fs.block.FileBlockMetaInfo;
import org.huel.cloudhub.file.fs.container.ContainerGroup;
import org.huel.cloudhub.file.fs.container.ContainerProvider;
import org.huel.cloudhub.file.fs.container.file.ContainerFileReader;
import org.huel.cloudhub.file.rpc.block.*;
import org.huel.cloudhub.server.file.FileProperties;
import org.huel.cloudhub.util.math.Maths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * @author RollW
 */
@Service
public class BlockDownloadService extends BlockDownloadServiceGrpc.BlockDownloadServiceImplBase {
    private final Logger logger = LoggerFactory.getLogger(BlockDownloadService.class);
    private final ContainerProvider containerProvider;
    private final FileProperties fileProperties;

    public BlockDownloadService(ContainerProvider containerProvider,
                                FileProperties fileProperties) {
        this.containerProvider = containerProvider;
        this.fileProperties = fileProperties;
    }

    @Override
    public void downloadBlocks(DownloadBlockRequest request,
                               StreamObserver<DownloadBlockResponse> responseObserver) {
        final String fileId = request.getFileId();
        ContainerGroup containerGroup =
                containerProvider.findContainerGroupByFile(fileId);
        FileBlockMetaInfo fileBlockMetaInfo = containerGroup.getFileBlockMetaInfo(fileId);
        if (fileBlockMetaInfo == null) {
            responseObserver.onError(Status.NOT_FOUND.asException());
            responseObserver.onCompleted();
            return;
        }

        final long fileLength = fileBlockMetaInfo.getFileLength();
        final long responseSize = fileProperties.getMaxRequestSizeBytes() >> 1;
        final int responseCount = Maths.ceilDivideReturnsInt(fileLength, responseSize);
        final int maxBlocksInResponse = (int) (responseSize / fileProperties.getBlockSizeInBytes());

        DownloadBlockResponse firstResponse = buildFirstResponse(fileBlockMetaInfo,
                responseCount,
                "0");
        responseObserver.onNext(firstResponse);

        try (ContainerFileReader containerFileReader = new ContainerFileReader(
                containerProvider, fileId, containerGroup, fileBlockMetaInfo)) {
            sendUtilEnd(responseObserver, containerFileReader, maxBlocksInResponse);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        responseObserver.onCompleted();
    }

    private void sendUtilEnd(StreamObserver<DownloadBlockResponse> responseObserver,
                             ContainerFileReader fileReader, int readSize) throws IOException {
        while (fileReader.hasNext()) {
            List<ContainerBlock> read = fileReader.read(readSize);
            if (read == null) {
                logger.info("read == null");
                return;
            }
            DownloadBlockResponse response = buildBlockDataResponse(read);
            logger.info("send download response. block size ={}", read.size());
            responseObserver.onNext(response);
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

    private DownloadBlockResponse buildBlockDataResponse(List<ContainerBlock> containerBlocks) {
        List<DownloadBlockData> downloadBlockData = new LinkedList<>();
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
                .build();
        return DownloadBlockResponse.newBuilder()
                .setDownloadBlocks(downloadBlocksInfo)
                .build();
    }


}
