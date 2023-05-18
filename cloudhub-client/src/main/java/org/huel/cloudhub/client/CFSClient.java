package org.huel.cloudhub.client;

import com.google.common.base.Strings;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.huel.cloudhub.client.rpc.file.FileStatusResponse;
import org.huel.cloudhub.client.server.ConnectedServers;
import org.huel.cloudhub.client.server.ContainerStatus;
import org.huel.cloudhub.client.server.FileServerLocation;
import org.huel.cloudhub.rpc.GrpcProperties;
import org.huel.cloudhub.server.DiskUsageInfo;
import org.huel.cloudhub.server.NetworkUsageInfo;
import org.huel.cloudhub.server.ServerHostInfo;
import org.huel.cloudhub.server.rpc.server.SerializedFileServer;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Cloudhub File System Client.
 *
 * @author RollW
 */
public class CFSClient implements Closeable {
    private final MetaServerConnection metaServerConnection;
    private final FileServerChannelPool channelPool;
    private final MetaFileStatusClient metaFileStatusClient;
    private final FileServerCheckClient fileServerCheckClient;
    private final ServerInfoCheckClient serverInfoCheckClient;
    private final ClientFileDeleteClient clientFileDeleteClient;
    private final ClientFileDownloadClient clientFileDownloadClient;
    private final ClientFileUploadClient clientFileUploadClient;

    public CFSClient(String host, int port) {
        this(new MetaServerConnection(host, port),
                new GrpcProperties(port, 64));
    }

    public CFSClient(String host, int port,
                     GrpcProperties grpcProperties) {
        this(new MetaServerConnection(host, port), grpcProperties);
    }

    public CFSClient(String target,
                     GrpcProperties grpcProperties) {
        this(new MetaServerConnection(target), grpcProperties);
    }

    public CFSClient(String target) {
        this(new MetaServerConnection(target),
                new GrpcProperties(0, 64));
    }

    public CFSClient(MetaServerConnection metaServerConnection,
                     GrpcProperties grpcProperties) {
        this.metaServerConnection = metaServerConnection;
        this.metaFileStatusClient = new MetaFileStatusClient(metaServerConnection);
        this.clientFileDeleteClient = new ClientFileDeleteClient(metaServerConnection);
        this.channelPool = new FileServerChannelPool(grpcProperties);
        this.clientFileDownloadClient = new ClientFileDownloadClient(channelPool, metaFileStatusClient);
        this.clientFileUploadClient = new ClientFileUploadClient(
                metaServerConnection,
                grpcProperties
        );
        this.fileServerCheckClient = new FileServerCheckClient(metaServerConnection, channelPool);
        this.serverInfoCheckClient = new ServerInfoCheckClient(metaServerConnection, channelPool, fileServerCheckClient);
    }

    public void deleteFile(String fileId) {
        clientFileDeleteClient.permanentlyDeleteFile(fileId);
    }

    // async
    public void uploadFile(InputStream inputStream,
                           String tempDirectory,
                           ClientFileUploadCallback clientFileUploadCallback)
            throws IOException {
        clientFileUploadClient.uploadFile(
                inputStream,
                tempDirectory,
                clientFileUploadCallback
        );
    }

    public FileValidation uploadFile(InputStream inputStream, String tempDirectory)
            throws IOException {
        CountDownLatch latch = new CountDownLatch(1);
        var object = new Object() {
            FileValidation fileValidation = null;
        };

        clientFileUploadClient.uploadFile(
                inputStream,
                tempDirectory,
                ((success, fileId, fileSize) -> {
                    if (success) {
                        object.fileValidation = new FileValidation(fileId, fileSize);
                    }
                    latch.countDown();
                })
        );
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return object.fileValidation;
    }

    public void downloadFile(OutputStream outputStream,
                             String fileId,
                             long startBytes, long endBytes,
                             ClientFileDownloadCallback clientFileDownloadCallback) throws IOException {
        clientFileDownloadClient.downloadFile(
                outputStream,
                fileId,
                startBytes,
                endBytes,
                clientFileDownloadCallback
        );
    }

    public CFSStatus downloadFile(OutputStream outputStream,
                                  String fileId,
                                  long startBytes, long endBytes) throws IOException {
        AtomicReference<CFSStatus> success = new AtomicReference<>(CFSStatus.UNKNOWN);
        CountDownLatch latch = new CountDownLatch(1);

        clientFileDownloadClient.downloadFile(
                outputStream,
                fileId,
                startBytes,
                endBytes,
                (sBool -> {
                    success.set(sBool);
                    latch.countDown();
                })
        );
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return success.get();
    }

    public void downloadFile(OutputStream outputStream,
                             String fileId,
                             ClientFileDownloadCallback clientFileDownloadCallback) throws IOException {

        clientFileDownloadClient.downloadFile(
                outputStream,
                fileId,
                clientFileDownloadCallback
        );
    }


    public CFSStatus downloadFile(OutputStream outputStream,
                                String fileId) throws IOException {
        AtomicReference<CFSStatus> success = new AtomicReference<>(CFSStatus.UNKNOWN);
        CountDownLatch latch = new CountDownLatch(1);

        clientFileDownloadClient.downloadFile(
                outputStream,
                fileId,
                (sBool -> {
                    success.set(sBool);
                    latch.countDown();
                })
        );

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return success.get();
    }

    public FileLocation getFileLocations(String fileId) throws IOException {
        FileStatusResponse response = metaFileStatusClient.getFileStatus(fileId);
        if (Strings.isNullOrEmpty(response.getMasterId())) {
            throw new CFSException("File not found: " + fileId);
        }
        return new FileLocation(
                fileId, response.getMasterId(),
                response.getServersList()
        );
    }

    public ConnectedServers getConnectedServers() {
        return fileServerCheckClient.getConnectedServers();
    }

    @Nullable
    public List<ContainerStatus> getContainerStatuses(String serverId) {
        return fileServerCheckClient.getContainerStatuses(serverId);
    }

    public List<FileServerLocation> getAllServers() {
        List<SerializedFileServer> servers =
                fileServerCheckClient.getAllServers();
        return servers.stream().map(FileServerLocation::from).toList();
    }

    public ServerHostInfo getMetaServerInfo() {
        return serverInfoCheckClient.getMetaServerInfo();
    }

    public List<NetworkUsageInfo> getMetaServerNetRecords() {
        return serverInfoCheckClient.getMetaServerNetRecords();
    }

    public List<DiskUsageInfo> getMetaServerDiskRecords() {
        return serverInfoCheckClient.getMetaServerDiskRecords();
    }

    public ServerHostInfo getFileServerInfo(String serverId) {
        return serverInfoCheckClient.getFileServerInfo(serverId);
    }

    public List<DiskUsageInfo> getFileServerDiskRecords(String serverId) {
        return serverInfoCheckClient.getFileServerDiskRecords(serverId);
    }

    public List<NetworkUsageInfo> getFileNetRecords(String serverId) {
        return serverInfoCheckClient.getFileNetRecords(serverId);
    }

    public MetaServerConnection getMetaServerConnection() {
        return metaServerConnection;
    }

    @Override
    public void close() throws IOException {
        metaServerConnection.close();
        channelPool.close();
    }
}
