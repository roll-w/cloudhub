package org.huel.cloudhub.client.disk.domain.tag;

import org.huel.cloudhub.client.disk.BaseAbility;
import org.huel.cloudhub.client.disk.domain.tag.dto.ContentTagInfo;
import org.huel.cloudhub.client.disk.domain.tag.dto.TagGroupInfo;
import org.huel.cloudhub.client.disk.domain.tag.dto.TagGroupDto;

import java.util.List;

/**
 * @author RollW
 */
@BaseAbility
public interface ContentTagProvider {
    List<ContentTagInfo> getTags();

    List<TagGroupInfo> getTagGroups();

    ContentTagInfo getTagById(long id);

    TagGroupDto getTagGroupById(long id);

    TagGroupInfo getTagGroupInfoById(long id);

    ContentTagInfo getByName(String name);

    List<TagGroupInfo> getTagGroupInfos(List<Long> tagGroupIds);

    List<ContentTagInfo> getTags(List<Long> tagIds);

    List<ContentTagInfo> getTagsByNames(List<String> names);

    List<TagGroupInfo> getTagGroupInfosByNames(List<String> names);
}
