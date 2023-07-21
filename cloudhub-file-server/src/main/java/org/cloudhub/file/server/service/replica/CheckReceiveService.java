/*
 * Cloudhub - A high available, scalable distributed file system.
 * Copyright (C) 2022 Cloudhub
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.cloudhub.file.server.service.replica;

import io.grpc.stub.StreamObserver;
import org.cloudhub.file.fs.LockException;
import org.cloudhub.file.fs.container.ContainerChecker;
import org.cloudhub.file.fs.container.ContainerFinder;
import org.cloudhub.file.fs.container.ContainerGroup;
import org.cloudhub.file.rpc.replica.CheckRequest;
import org.cloudhub.file.rpc.replica.CheckResponse;
import org.cloudhub.file.rpc.replica.CheckServiceGrpc;
import org.cloudhub.file.rpc.replica.SerializedContainerCheckStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author RollW
 */
@Service
public class CheckReceiveService extends CheckServiceGrpc.CheckServiceImplBase {
    private final ContainerFinder containerFinder;
    private final ContainerChecker containerChecker;

    // TODO: check source

    public CheckReceiveService(ContainerFinder containerFinder, ContainerChecker containerChecker) {
        this.containerFinder = containerFinder;
        this.containerChecker = containerChecker;
    }

    @Override
    public void checkContainers(CheckRequest request, StreamObserver<CheckResponse> responseObserver) {
        final String id = request.getContainerId();
        List<Long> serials = request.getSerialsList();
        final String source = getSource(request);
        ContainerGroup group =
                containerFinder.findContainerGroup(id, source);
        if (group == null) {
            responseObserver.onNext(CheckResponse.newBuilder()
                    .build());
            responseObserver.onCompleted();
            return;
        }
        List<SerializedContainerCheckStatus> statuses = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(group.containers().size());
        group.containers().stream()
                .filter(container -> serials.contains(container.getSerial()))
                .parallel().forEach(container -> {
                    String checkValue = null;
                    boolean metaDamaged = false;
                    try {
                        checkValue = containerChecker.calculateChecksum(container);
                    } catch (LockException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                        metaDamaged = true;
                    }
                    SerializedContainerCheckStatus status = SerializedContainerCheckStatus.newBuilder()
                            .setMetaDamaged(metaDamaged)
                            .setSerial(container.getSerial())
                            .setCheckValue(checkValue)
                            .build();
                    statuses.add(status);
                    latch.countDown();
                });
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        responseObserver.onNext(CheckResponse.newBuilder()
                .addAllStatus(statuses)
                .build());
        responseObserver.onCompleted();

    }

    private String getSource(CheckRequest request) {
        if (request.hasSource()) {
            return request.getSource();
        }
        return ContainerFinder.LOCAL;
    }
}
