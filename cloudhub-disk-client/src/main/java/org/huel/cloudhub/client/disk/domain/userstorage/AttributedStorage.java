package org.huel.cloudhub.client.disk.domain.userstorage;

/**
 * @author RollW
 */
public interface AttributedStorage extends Storage {
    long getCreateTime();

    long getUpdateTime();

    boolean isDeleted();
}
