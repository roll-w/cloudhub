package org.huel.cloudhub.client.disk.domain.storagesearch;

import org.huel.cloudhub.client.disk.domain.storagesearch.common.SearchConditionException;
import org.huel.cloudhub.client.disk.domain.userstorage.AttributedStorage;

import java.util.List;

/**
 * @author RollW
 */
public interface StorageSearchConditionProvider {
    List<? extends AttributedStorage> getStorages(SearchConditionGroup conditionGroup)
            throws SearchConditionException;

    boolean supportsCondition(String name);
}
