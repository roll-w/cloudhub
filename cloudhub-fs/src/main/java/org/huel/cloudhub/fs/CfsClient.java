package org.huel.cloudhub.fs;

import com.google.common.base.Strings;
import org.huel.cloudhub.client.rpc.file.FileStatusResponse;
import org.huel.cloudhub.rpc.GrpcProperties;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Cloudhub File System Client.
 *
 * @author RollW
 */
public class CfsClient {
    private final MetaServerConnection metaServerConnection;
    private final MetaFileStatusClient metaFileStatusClient;
    private final ClientFileDeleteClient clientFileDeleteClient;
    private final ClientFileDownloadClient clientFileDownloadClient;
    private final ClientFileUploadClient clientFileUploadClient;

    public CfsClient(String host, int port) {
        this(new MetaServerConnection(host, port),
                new GrpcProperties(port, 64));
    }

    public CfsClient(String host, int port,
                     GrpcProperties grpcProperties) {
        this(new MetaServerConnection(host, port), grpcProperties);
    }

    public CfsClient(MetaServerConnection metaServerConnection,
                     GrpcProperties grpcProperties) {
        this.metaServerConnection = metaServerConnection;
        this.metaFileStatusClient = new MetaFileStatusClient(metaServerConnection);
        this.clientFileDeleteClient = new ClientFileDeleteClient(metaServerConnection);
        FileServerChannelPool channelPool = new FileServerChannelPool(null);
        this.clientFileDownloadClient = new ClientFileDownloadClient(channelPool, metaFileStatusClient);
        this.clientFileUploadClient = new ClientFileUploadClient(
                metaServerConnection,
                grpcProperties,
                null
        );
    }

    public FileLocation getFileLocations(String fileId) throws IOException {
        FileStatusResponse response = metaFileStatusClient.getFileStatus(fileId);
        if (Strings.isNullOrEmpty(response.getMasterId())) {
            throw new CfsException("File not found: " + fileId);
        }
        return new FileLocation(
                fileId, response.getMasterId(),
                response.getServersList()
        );
    }

    public void deleteFile(String fileId) {
        clientFileDeleteClient.permanentlyDeleteFile(fileId);
    }

    // async
    public void uploadFile(InputStream inputStream,
                           ClientFileUploadCallback clientFileUploadCallback)
            throws IOException {
        clientFileUploadClient.uploadFile(
                inputStream,
                clientFileUploadCallback
        );
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

    public void downloadFile(OutputStream outputStream,
                             String fileId,
                             ClientFileDownloadCallback clientFileDownloadCallback) throws IOException {
        clientFileDownloadClient.downloadFile(
                outputStream,
                fileId,
                clientFileDownloadCallback
        );
    }

    public MetaServerConnection getMetaServerConnection() {
        return metaServerConnection;
    }
}
