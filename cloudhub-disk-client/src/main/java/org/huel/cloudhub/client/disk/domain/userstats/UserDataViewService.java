package org.huel.cloudhub.client.disk.domain.userstats;

import org.huel.cloudhub.client.disk.domain.userstats.dto.RestrictInfo;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageOwner;

import java.util.List;

/**
 * @author RollW
 */
public interface UserDataViewService {
    RestrictInfo findRestrictOf(StorageOwner storageOwner, String key);

    List<RestrictInfo> findRestrictsOf(StorageOwner storageOwner);
}
