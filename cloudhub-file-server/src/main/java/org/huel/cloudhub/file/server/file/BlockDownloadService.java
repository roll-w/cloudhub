package org.huel.cloudhub.file.server.file;

import com.google.protobuf.ByteString;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.huel.cloudhub.file.fs.block.BlockGroup;
import org.huel.cloudhub.file.fs.block.BlockMetaInfo;
import org.huel.cloudhub.file.fs.block.ContainerBlock;
import org.huel.cloudhub.file.fs.block.FileBlockMetaInfo;
import org.huel.cloudhub.file.fs.container.Container;
import org.huel.cloudhub.file.fs.container.ContainerGroup;
import org.huel.cloudhub.file.fs.container.ContainerProvider;
import org.huel.cloudhub.file.fs.container.ContainerReader;
import org.huel.cloudhub.file.io.IoUtils;
import org.huel.cloudhub.file.rpc.block.*;
import org.huel.cloudhub.server.file.FileProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

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
        List<Container> containers = containerGroup.containersWithFile(fileId);

        final long fileLength = fileBlockMetaInfo.getFileLength();
        final long responseSize = fileProperties.getMaxRequestSizeBytes() >> 1;
        final int responseCount = (int) Math.ceil(fileLength * 1.0d / responseSize);
        final int maxBlocksInResponse = (int) (responseSize / fileProperties.getBlockSizeInBytes());

        DownloadBlockResponse firstResponse = buildFirstResponse(fileBlockMetaInfo,
                responseCount,
                "0");
        responseObserver.onNext(firstResponse);
        ListIterator<Container> containerIterator =
                containers.listIterator();
        ContainerReader currentReader = null;
        ReadBlockDest lastRead = null;

        while (containerIterator.hasNext()) {
            ReadResult res;
            try {
                res = readSizeOf(currentReader, containerIterator,
                        maxBlocksInResponse, lastRead, fileBlockMetaInfo);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (res == null) {
                break;
            }
            currentReader = res.reader();
            lastRead = res.readBlockDest();

            DownloadBlockResponse response =
                    buildBlockDataResponse(res.containerBlocks());
            responseObserver.onNext(response);
        }

        IoUtils.closeQuietly(currentReader);
        responseObserver.onCompleted();
    }

    private record ReadBlockDest(long serial, int endIndex) {
    }

    private record ReadResult(List<ContainerBlock> containerBlocks,
                              ContainerReader reader,
                              ReadBlockDest readBlockDest) {
    }

    // 读取给定块数
    private ReadResult readSizeOf(@Nullable ContainerReader reader,
                                  ListIterator<Container> containerIterator,
                                  int readBlockCount,
                                  ReadBlockDest lastRead,
                                  FileBlockMetaInfo fileBlockMetaInfo) throws IOException {
        ContainerReader containerReader = reader;
        if (reader == null) {
            containerReader = openNext(containerIterator);
        }
        if (containerReader == null) {
            return null;
        }

        List<ContainerBlock> containerBlocksRes = new LinkedList<>();
        final int start = lastRead == null ? 1 : lastRead.endIndex();

        List<BlockMetaInfo> blockMetaInfos = findBlockCount(
                fileBlockMetaInfo, start, readBlockCount);

        int index = 0, readBlocks = 0;
        final int maxIndex = blockMetaInfos.size() - 1;
        for (BlockMetaInfo blockMetaInfo : blockMetaInfos) {
            if (index != 0) {
                containerReader = openNext(containerIterator);
            }
            var readResult = readBlocks(
                    containerReader, blockMetaInfo, start,
                    readBlockCount - readBlocks);
            readBlocks += readResult.containerBlocks().size();
            containerBlocksRes.addAll(readResult.containerBlocks());

            if (index != maxIndex) {
                IoUtils.closeQuietly(containerReader);
            } else {
                return new ReadResult(containerBlocksRes, containerReader,
                        readResult.readBlockDest());
            }

            index++;
        }

        return null;
    }

    private ReadResult readBlocks(final ContainerReader reader,
                                  BlockMetaInfo blockMetaInfo,
                                  int start,
                                  int toReadSize) throws IOException {
        List<ContainerBlock> containerBlocks = new LinkedList<>();
        int index = 0, sizeRead = 0, endBlock = 0;
        List<BlockGroup> blockGroups = blockMetaInfo.getBlockGroups();
        for (BlockGroup blockGroup : blockGroups) {
            final int startBlock = index == 0
                    ? start : blockGroup.start();
            final int willReadBlocks = index == 0
                    ? blockGroup.end() - startBlock
                    : blockGroup.occupiedBlocks();

            if (sizeRead + willReadBlocks >= toReadSize) {
                endBlock = toReadSize - sizeRead + startBlock;
                containerBlocks.addAll(
                        reader.readBlocks(blockGroup.start(), endBlock));

                return new ReadResult(containerBlocks, reader, new ReadBlockDest(
                        blockMetaInfo.getContainerSerilal(), endBlock));
            }

            endBlock = blockGroup.end();
            List<ContainerBlock> readBlocks =
                    reader.readBlocks(blockGroup.start(), blockGroup.end());
            sizeRead += willReadBlocks;
            containerBlocks.addAll(readBlocks);
            index++;
        }

        return new ReadResult(containerBlocks, reader, new ReadBlockDest(
                blockMetaInfo.getContainerSerilal(), endBlock));
    }

    private List<BlockMetaInfo> findBlockCount(FileBlockMetaInfo fileBlockMetaInfo, int start, int blockCount) {
        if (start <= 1) {
            start = 1;
        }

        List<BlockMetaInfo> blockMetaInfos = fileBlockMetaInfo.getAfter(start);
        List<BlockMetaInfo> res = new ArrayList<>();
        int blocks = 0;
        for (BlockMetaInfo blockMetaInfo : blockMetaInfos) {
            if (blocks >= blockCount) {
                return res;
            }
            if (blocks + blockMetaInfo.occupiedBlocks() >= blockCount) {
                res.add(blockMetaInfo);
                return res;
            }
            blocks += blockMetaInfo.occupiedBlocks();
            res.add(blockMetaInfo);
        }
        return res;
    }

    private ContainerReader openNext(ListIterator<Container> containerIterator) throws IOException {
        if (!containerIterator.hasNext()) {
            return null;
        }
        return new ContainerReader(containerIterator.next(), containerProvider);
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
