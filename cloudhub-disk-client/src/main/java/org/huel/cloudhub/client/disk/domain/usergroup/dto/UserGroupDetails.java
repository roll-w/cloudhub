package org.huel.cloudhub.client.disk.domain.usergroup.dto;

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
        List<StorageOwner> members
) {
}
