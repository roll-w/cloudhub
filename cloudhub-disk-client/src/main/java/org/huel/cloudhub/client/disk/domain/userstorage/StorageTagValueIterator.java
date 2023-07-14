package org.huel.cloudhub.client.disk.domain.userstorage;

import org.huel.cloudhub.client.disk.domain.userstorage.dto.StorageTagValue;

import java.util.Iterator;
import java.util.List;

/**
 * @author RollW
 */
public interface StorageTagValueIterator
        extends Iterator<List<StorageTagValue>> {
    @Override
    boolean hasNext();

    @Override
    List<StorageTagValue> next();
}
