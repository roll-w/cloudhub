package org.huel.cloudhub.client.disk.domain.userstorage;

import org.huel.cloudhub.client.disk.domain.operatelog.Operator;
import org.huel.cloudhub.client.disk.domain.userstorage.dto.FileAttributesInfo;
import org.huel.cloudhub.web.ErrorCode;

/**
 * @author RollW
 */
public interface StoragePreInterceptor {
    ErrorCode onBeforeStorageCreated(StorageOwner storageOwner,
                                     Operator operator,
                                     FileAttributesInfo fileAttributesInfo);
}
