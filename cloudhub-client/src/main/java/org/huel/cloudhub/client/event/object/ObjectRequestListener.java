package org.huel.cloudhub.client.event.object;

import org.springframework.context.event.EventListener;
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
    public void onPut(ObjectPutEvent objectPutEvent) {
        putCount.incrementAndGet();
    }

    @EventListener
    public void onGet(ObjectGetEvent objectGetEvent) {
        getCount.incrementAndGet();
    }

    @EventListener
    public void onGet(ObjectDeleteEvent objectDeleteEvent) {
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
