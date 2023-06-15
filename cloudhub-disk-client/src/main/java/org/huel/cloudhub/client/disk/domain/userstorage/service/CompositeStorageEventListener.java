package org.huel.cloudhub.client.disk.domain.userstorage.service;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.huel.cloudhub.client.disk.domain.operatelog.Operator;
import org.huel.cloudhub.client.disk.domain.userstorage.Storage;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageEventListener;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageOwner;
import org.huel.cloudhub.client.disk.domain.userstorage.dto.FileAttributesInfo;
import org.huel.cloudhub.client.disk.domain.userstorage.dto.StorageAttr;
import org.huel.cloudhub.web.CommonErrorCode;
import org.huel.cloudhub.web.ErrorCode;
import space.lingu.NonNull;

import java.util.List;

/**
 * @author RollW
 */
public class CompositeStorageEventListener implements StorageEventListener {
    private final List<StorageEventListener> storageEventListeners;

    public CompositeStorageEventListener(List<StorageEventListener> storageEventListeners) {
        this.storageEventListeners = storageEventListeners;
    }

    @Override
    public ErrorCode onBeforeStorageCreated(@NonNull StorageOwner storageOwner, @NonNull Operator operator, FileAttributesInfo fileAttributesInfo) {
        for (StorageEventListener storageEventListener : storageEventListeners) {
            ErrorCode errorCode = storageEventListener.onBeforeStorageCreated(storageOwner, operator, fileAttributesInfo);
            if (errorCode.failed()) {
                return errorCode;
            }
        }
        return CommonErrorCode.SUCCESS;
    }

    @Override
    public void onStorageCreated(@NonNull Storage storage, StorageAttr storageAttr) {
        for (StorageEventListener storageEventListener : storageEventListeners) {
            storageEventListener.onStorageCreated(storage, storageAttr);
        }
    }

    @Override
    public void onStorageProcess(Storage storage, @Nullable StorageAttr storageAttr) {
        for (StorageEventListener storageEventListener : storageEventListeners) {
            storageEventListener.onStorageProcess(storage, storageAttr);
        }
    }

    @Override
    public void onStorageDeleted(@NonNull Storage storage, @Nullable FileAttributesInfo fileAttributesInfo) {
        for (StorageEventListener storageEventListener : storageEventListeners) {
            storageEventListener.onStorageDeleted(storage, fileAttributesInfo);
        }
    }
}
