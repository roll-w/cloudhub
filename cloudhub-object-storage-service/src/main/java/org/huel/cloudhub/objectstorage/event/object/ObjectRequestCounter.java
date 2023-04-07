package org.huel.cloudhub.objectstorage.event.object;

/**
 * @author RollW
 */
public interface ObjectRequestCounter {
    long getPutCount();

    long getGetCount();

    long getDeleteCount();

    default long getAllRequestCount() {
        return getPutCount() + getGetCount() + getDeleteCount();
    }
}
