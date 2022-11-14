package org.huel.cloudhub.rpc;

import io.grpc.stub.StreamObserver;

/**
 * @author RollW
 */
public class StreamObserverWrapper<V> implements StreamObserver<V> {
    private boolean close = false;
    private final StreamObserver<V> streamObserver;

    public StreamObserverWrapper(StreamObserver<V> streamObserver) {
        this.streamObserver = streamObserver;
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

    public boolean isClose() {
        return close;
    }

    public boolean isOpen() {
        return !close;
    }

    public StreamObserver<V> getStreamObserver() {
        return streamObserver;
    }
}
