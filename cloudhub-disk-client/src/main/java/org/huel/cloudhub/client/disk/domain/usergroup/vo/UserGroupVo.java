package org.huel.cloudhub.client.disk.domain.usergroup.vo;

import org.huel.cloudhub.client.disk.domain.usergroup.dto.UserGroupInfo;
import org.huel.cloudhub.web.KeyValue;

import java.util.List;

/**
 * @author RollW
 */
public record UserGroupVo(
        long id,
        String name,
        String description,
        List<KeyValue> settings,
        long createTime,
        long updateTime
) {
    public static UserGroupVo from(UserGroupInfo userGroupInfo) {
        return new UserGroupVo(
                userGroupInfo.id(),
                userGroupInfo.name(),
                userGroupInfo.description(),
                KeyValue.from(userGroupInfo.settings()),
                userGroupInfo.createTime(),
                userGroupInfo.updateTime()
        );
    }
}
