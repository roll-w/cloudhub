package org.huel.cloudhub.client.disk.system.pages;

/**
 * @author RollW
 */
public interface CountableDao<T> {
    /**
     * Count the number of records.
     *
     * @return the number of records.
     */
    long getCount();

    long getActiveCount();

    Class<T> getCountableType();
}
