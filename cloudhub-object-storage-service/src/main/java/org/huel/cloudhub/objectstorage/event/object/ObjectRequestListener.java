package org.huel.cloudhub.objectstorage.event.object;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author RollW
 */
@Component
public class ObjectRequestListener implements ObjectRequestCounter {
    private final AtomicLong putCount = new AtomicLong(0);
    private final AtomicLong getCount = new AtomicLong(0);
    private final AtomicLong deleteCount = new AtomicLong(0);

    @EventListener
    @Async
    public void onPut(ObjectPutRequestEvent objectPutRequestEvent) {
        putCount.incrementAndGet();
    }

    @EventListener
    @Async
    public void onGet(ObjectGetRequestEvent objectGetRequestEvent) {
        getCount.incrementAndGet();
    }

    @EventListener
    @Async
    public void onGet(ObjectDeleteRequestEvent objectDeleteRequestEvent) {
        deleteCount.incrementAndGet();
    }

    @Override
    public long getPutCount() {
        return putCount.get();
    }

    @Override
    public long getGetCount() {
        return getCount.get();
    }

    @Override
    public long getDeleteCount() {
        return deleteCount.get();
    }

}
