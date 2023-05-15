package org.huel.cloudhub.rpc;

import io.grpc.Status;
import io.grpc.stub.ServerCallStreamObserver;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author RollW
 */
public class StreamObserverWrapper<V> implements StreamObserver<V> {
    private volatile boolean close = false;
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
        close();
        streamObserver.onError(t);
    }

    @Override
    public void onCompleted() {
        if (close) {
            return;
        }
        close();
        streamObserver.onCompleted();
    }

    public void setClose() {
        close = true;
    }

    public void cancel() {
        onError(Status.CANCELLED.asRuntimeException());
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

    public static final int WAIT_FOR_READY_TIMEOUT = 10000;

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
        try {
            CountDownLatch countDownLatch = new CountDownLatch(1);
            callStreamObserver.setOnReadyHandler(countDownLatch::countDown);
            boolean state = countDownLatch.await(500, TimeUnit.MILLISECONDS);
            if (state) {
                return;
            }
        } catch (InterruptedException e) {
            throw e;
        } catch (Exception ignored) {
            // it may not allow setting the handler
        }

        // fallback to busy wait
        int i = 0;
        while (!callStreamObserver.isReady()) {
            // wait for ready
            // TODO: is there a better way to do this?
            if (i++ > WAIT_FOR_READY_TIMEOUT) {
                // wait for max 10000 times
                return;
            }
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
