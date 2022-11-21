package org.huel.cloudhub.client.service.rpc;

import io.grpc.ManagedChannel;
import org.huel.cloudhub.client.rpc.file.ClientFileUploadServiceGrpc;
import org.huel.cloudhub.rpc.GrpcProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author RollW
 */
@Service
public class ClientFileUploadService {
    private final GrpcProperties grpcProperties;
    private final ClientFileUploadServiceGrpc.ClientFileUploadServiceStub stub;
    private final Logger logger = LoggerFactory.getLogger(ClientFileUploadService.class);

    public ClientFileUploadService(ManagedChannel managedChannel,
                                   GrpcProperties grpcProperties) {
        this.grpcProperties = grpcProperties;
        this.stub = ClientFileUploadServiceGrpc.newStub(managedChannel);
    }


    public void uploadFile(InputStream inputStream) throws IOException {
        logger.debug("Start calculation on the given input stream.");
        // TODO:
    }

}
