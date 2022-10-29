package org.huel.cloudhub.meta.server.service.file;

import io.grpc.ManagedChannel;
import io.grpc.stub.StreamObserver;
import org.huel.cloudhub.file.rpc.block.*;
import org.huel.cloudhub.meta.server.service.node.HeartbeatService;
import org.huel.cloudhub.meta.server.service.node.NodeChannelPool;
import org.huel.cloudhub.server.file.FileProperties;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * @author RollW
 */
@Service
public class FileDownloadService {
    private final HeartbeatService heartbeatService;
    private final FileProperties fileProperties;
    private final NodeChannelPool nodeChannelPool;

    public FileDownloadService(HeartbeatService heartbeatService,
                               FileProperties fileProperties) {
        this.heartbeatService = heartbeatService;
        this.fileProperties = fileProperties;
        this.nodeChannelPool = new NodeChannelPool();
    }

    public void downloadFile(OutputStream outputStream, String fileId) {
        DownloadBlockRequest request = DownloadBlockRequest.newBuilder()
                .setFileId(fileId)
                .build();
        BlockDownloadServiceGrpc.BlockDownloadServiceStub stub =
                requireStub(fileId);

        stub.downloadBlocks(request, new DownloadBlockStreamObserver());
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

        private void saveCheckMessage(DownloadBlockResponse.CheckMessage checkMessage) {
            // TODO:
        }

        @Override
        public void onNext(DownloadBlockResponse value) {
            if (value.getDownloadMessageCase() ==
                    DownloadBlockResponse.DownloadMessageCase.DOWNLOADMESSAGE_NOT_SET) {

                return;
            }

            if (value.getDownloadMessageCase() ==
                    DownloadBlockResponse.DownloadMessageCase.CHECK_MESSAGE) {
                saveCheckMessage(value.getCheckMessage());
                return;
            }
            DownloadBlocksInfo downloadBlocksInfo = value.getDownloadBlocks();

        }

        @Override
        public void onError(Throwable t) {

        }

        @Override
        public void onCompleted() {
            // TODO: check file.
        }


    }

    private void writeTo(List<DownloadBlockData> downloadBlockData, OutputStream stream) throws IOException {
        for (DownloadBlockData downloadBlockDatum : downloadBlockData) {
            stream.write(downloadBlockDatum.getData().toByteArray());
        }
    }
}
