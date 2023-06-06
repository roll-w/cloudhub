package org.huel.cloudhub.client.disk.domain.storagesearch.service;

import org.huel.cloudhub.client.disk.domain.storagesearch.SearchCondition;
import org.huel.cloudhub.client.disk.domain.storagesearch.SearchConditionGroup;
import org.huel.cloudhub.client.disk.domain.storagesearch.StorageSearchConditionProvider;
import org.huel.cloudhub.client.disk.domain.storagesearch.StorageSearchService;
import org.huel.cloudhub.client.disk.domain.storagesearch.common.SearchConditionException;
import org.huel.cloudhub.client.disk.domain.userstorage.AttributedStorage;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageOwner;
import org.huel.cloudhub.client.disk.domain.userstorage.dto.SimpleStorageIdentity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author RollW
 */
@Service
public class StorageSearchServiceImpl implements StorageSearchService {
    private final List<StorageSearchConditionProvider> storageSearchConditionProviders;

    public StorageSearchServiceImpl(List<StorageSearchConditionProvider> storageSearchConditionProviders) {
        this.storageSearchConditionProviders = storageSearchConditionProviders;
    }

    @Override
    public List<? extends AttributedStorage> searchFor(
            List<SearchCondition> searchConditions, StorageOwner storageOwner) {
        if (searchConditions.isEmpty()) {
            return List.of();
        }
        Set<StorageSearchConditionProvider> collected =
                getProviders(searchConditions);

        SearchConditionGroup conditionGroup = new SearchConditionGroup(searchConditions);
        if (conditionGroup.hasDuplicateConditionName()) {
            return List.of();
        }

        List<AttributedStorage> result = new ArrayList<>();
        for (StorageSearchConditionProvider storageSearchConditionProvider : collected) {
            List<? extends AttributedStorage> attributedStorages =
                    storageSearchConditionProvider.getStorages(conditionGroup, storageOwner);
            result.addAll(attributedStorages);
        }
        return distinctByIdAndType(result);
    }

    private List<? extends AttributedStorage> distinctByIdAndType(List<AttributedStorage> result) {
        Set<SimpleStorageIdentity> existStorages = new HashSet<>();
        List<AttributedStorage> distinctResult = new ArrayList<>();
        for (AttributedStorage attributedStorage : result) {
            SimpleStorageIdentity simpleStorageIdentity = new SimpleStorageIdentity(attributedStorage.getStorageId(),
                    attributedStorage.getStorageType());
            if (!existStorages.contains(simpleStorageIdentity)) {
                distinctResult.add(attributedStorage);
                existStorages.add(simpleStorageIdentity);
            }
        }
        return distinctResult;
    }

    @Override
    public List<? extends AttributedStorage> searchFor(
            List<SearchCondition> searchConditions) throws SearchConditionException {
        return null;
    }

    private Set<StorageSearchConditionProvider> getProviders(
            List<SearchCondition> searchConditions) {
        Set<StorageSearchConditionProvider> collected = new HashSet<>();
        for (SearchCondition searchCondition : searchConditions) {
            StorageSearchConditionProvider provider = findFirst(searchCondition.name());
            if (provider != null) {
                collected.add(provider);
            }
        }
        return collected;
    }

    private StorageSearchConditionProvider findFirst(String condition) {
        return storageSearchConditionProviders.stream()
                .filter(storageSearchConditionProvider -> storageSearchConditionProvider
                        .supportsCondition(condition))
                .findFirst()
                .orElse(null);
    }
}
