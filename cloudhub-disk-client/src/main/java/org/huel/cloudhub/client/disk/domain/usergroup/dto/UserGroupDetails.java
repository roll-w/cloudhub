package org.huel.cloudhub.client.disk.domain.usergroup.dto;

import org.huel.cloudhub.client.disk.domain.systembased.SystemResource;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResourceKind;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageOwner;

import java.util.List;
import java.util.Map;

/**
 * @author RollW
 */
public record UserGroupDetails(
        long id,
        String name,
        String description,
        Map<String, String> settings,
        long createTime,
        long updateTime,
        boolean deleted,
        List<StorageOwner> members
) implements SystemResource {
    @Override
    public long getResourceId() {
        return id;
    }

    @Override
    public SystemResourceKind getSystemResourceKind() {
        return SystemResourceKind.USER_GROUP;
    }
}
