package org.huel.cloudhub.client.disk.domain.storagesearch;

import org.huel.cloudhub.client.disk.domain.storagesearch.common.SearchConditionException;
import org.huel.cloudhub.client.disk.domain.userstorage.AttributedStorage;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageOwner;

import java.util.List;

/**
 * @author RollW
 */
public interface StorageSearchConditionProvider {
    List<? extends AttributedStorage> getStorages(SearchConditionGroup conditionGroup,
                                                  StorageOwner storageOwner)
            throws SearchConditionException;

    boolean supportsCondition(String name);
}
