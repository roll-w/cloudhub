package org.huel.cloudhub.client.disk.domain.userstorage;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.huel.cloudhub.client.disk.domain.operatelog.Operator;
import org.huel.cloudhub.client.disk.domain.userstorage.dto.FileAttributesInfo;
import org.huel.cloudhub.client.disk.domain.userstorage.dto.StorageAttr;
import org.huel.cloudhub.web.CommonErrorCode;
import org.huel.cloudhub.web.ErrorCode;
import space.lingu.NonNull;

/**
 * @author RollW
 */
public interface StorageEventListener {
    default ErrorCode onBeforeStorageCreated(@NonNull StorageOwner storageOwner,
                                             @NonNull Operator operator,
                                             @Nullable FileAttributesInfo fileAttributesInfo) {
        return CommonErrorCode.SUCCESS;
    }

    default void onStorageCreated(@NonNull Storage storage,
                                  @Nullable StorageAttr storageAttr) {
    }

    default void onStorageDeleted(@NonNull Storage storage,
                                  @Nullable FileAttributesInfo fileAttributesInfo) {
    }

    default void onStorageProcess(Storage storage,
                                  @Nullable StorageAttr storageAttr) {
    }
}
