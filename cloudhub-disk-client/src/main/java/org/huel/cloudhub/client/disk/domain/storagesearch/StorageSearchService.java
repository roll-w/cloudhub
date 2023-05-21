package org.huel.cloudhub.client.disk.domain.storagesearch;

import org.huel.cloudhub.client.disk.domain.storagesearch.common.SearchConditionException;
import org.huel.cloudhub.client.disk.domain.userstorage.AttributedStorage;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageOwner;

import java.util.List;

/**
 * @author RollW
 */
public interface StorageSearchService {
    List<? extends AttributedStorage> searchFor(List<SearchCondition> searchConditions,
                                                StorageOwner storageOwner)
            throws SearchConditionException;

    List<? extends AttributedStorage> searchFor(List<SearchCondition> searchConditions)
            throws SearchConditionException;
}
