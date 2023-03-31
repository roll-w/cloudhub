package org.huel.cloudhub.rpc;

import io.grpc.stub.ServerCallStreamObserver;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author RollW
 */
public class StreamObserverWrapper<V> implements StreamObserver<V> {
    private boolean close = false;
    private final StreamObserver<V> streamObserver;
    private final ServerCallStreamObserver<V> callStreamObserver;

    public StreamObserverWrapper(StreamObserver<V> streamObserver) {
        this.streamObserver = streamObserver;
        if (streamObserver instanceof ServerCallStreamObserver) {
            this.callStreamObserver = (ServerCallStreamObserver<V>) streamObserver;
        } else {
            this.callStreamObserver = null;
        }
    }

    private void close() {
        close = true;
    }

    @Override
    public void onNext(V value) {
        if (close) {
            return;
        }
        streamObserver.onNext(value);
    }

    @Override
    public void onError(Throwable t) {
        if (close) {
            return;
        }
        streamObserver.onError(t);
        close();
    }

    @Override
    public void onCompleted() {
        if (close) {
            return;
        }
        streamObserver.onCompleted();
        close();
    }

    public boolean isReady() {
        if (callStreamObserver == null) {
            return true;
        }
        return callStreamObserver.isReady();
    }

    public void setOnReadyHandler(Runnable runnable) {
        if (callStreamObserver == null) {
            return;
        }
        callStreamObserver.setOnReadyHandler(runnable);
    }

    public void waitForReady() throws InterruptedException {
        if (isClose()) {
            return;
        }
        if (callStreamObserver == null) {
            return;
        }
        if (callStreamObserver.isReady()) {
            return;
        }
        CountDownLatch countDownLatch = new CountDownLatch(1);
        callStreamObserver.setOnReadyHandler(countDownLatch::countDown);
        boolean state = countDownLatch.await(500, TimeUnit.MILLISECONDS);
        if (state) {
            return;
        }
        while (!callStreamObserver.isReady()) {
        }
    }

    public void setOnCancelHandler(Runnable runnable) {
        if (callStreamObserver == null) {
            return;
        }
        callStreamObserver.setOnCancelHandler(runnable);
    }

    public void disableAutoInboundFlowControl() {
        if (callStreamObserver == null) {
            return;
        }
        callStreamObserver.disableAutoInboundFlowControl();
    }

    public void request(int numMessages) {
        if (callStreamObserver == null) {
            return;
        }
        callStreamObserver.request(numMessages);
    }

    public void setMessageCompression(boolean enable) {
        if (callStreamObserver == null) {
            return;
        }
        callStreamObserver.setMessageCompression(enable);
    }

    public boolean isCancelled() {
        if (callStreamObserver == null) {
            return false;
        }
        return callStreamObserver.isCancelled();
    }

    public boolean isClose() {
        if (close) {
            return true;
        }
        return isCancelled();
    }

    public boolean isOpen() {
        if (close) {
            return false;
        }
        return !isCancelled();
    }

    public StreamObserver<V> getStreamObserver() {
        return streamObserver;
    }
}
