package org.huel.cloudhub.client.disk.domain.share;

import org.huel.cloudhub.client.disk.domain.share.dto.SharePasswordInfo;
import org.huel.cloudhub.client.disk.domain.share.dto.ShareStructureInfo;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageIdentity;
import org.huel.cloudhub.web.data.page.Pageable;

import java.util.List;

/**
 * @author RollW
 */
public interface ShareSearchService {
    SharePasswordInfo search(String shareCode);

    SharePasswordInfo findById(long shareId);

    List<SharePasswordInfo> findByUserId(
            long userId, Pageable pageable);

    List<SharePasswordInfo> findByUserId(long userId);

    List<SharePasswordInfo> findByStorage(
            StorageIdentity storageIdentity);

    ShareStructureInfo findStructureById(long shareId, long parentId);

    ShareStructureInfo findStructureByShareCode(String shareCode, long parentId);
}
