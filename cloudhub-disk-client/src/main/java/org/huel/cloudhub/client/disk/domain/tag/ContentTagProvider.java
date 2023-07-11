package org.huel.cloudhub.client.disk.domain.tag;

import org.huel.cloudhub.client.disk.domain.tag.dto.ContentTagInfo;
import org.huel.cloudhub.client.disk.domain.tag.dto.TagGroupInfo;
import org.huel.cloudhub.client.disk.domain.tag.dto.TagGroupDto;

import java.util.List;

/**
 * @author RollW
 */
public interface ContentTagProvider {
    ContentTagInfo getTagById(long id);

    TagGroupDto getTagGroupById(long id);

    TagGroupInfo getTagGroupInfoById(long id);

    ContentTagInfo getByName(String name);

    List<TagGroupInfo> getTagGroupInfos(List<Long> tagGroupIds);

    List<ContentTagInfo> getTags(List<Long> tagIds);
}
