package org.huel.cloudhub.client.disk.domain.tag;

import org.huel.cloudhub.client.disk.domain.tag.dto.ContentTagDto;
import org.huel.cloudhub.client.disk.domain.tag.dto.ContentTagGroupInfo;
import org.huel.cloudhub.client.disk.domain.tag.dto.ContentTagInfo;
import org.huel.cloudhub.client.disk.domain.tag.dto.TagGroupDto;
import org.huel.cloudhub.web.data.page.Pageable;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * @author RollW
 */
public interface ContentTagService {
    ContentTagDto getTagById(long id);

    TagGroupDto getTagGroupById(long id);

    List<ContentTagDto> getTags(Pageable pageable);

    List<TagGroupDto> getTagGroups(Pageable pageable);

    ContentTagDto getByName(String name);

    void createContentTagGroup(ContentTagGroupInfo tagGroupInfo);

    void createContentTag(ContentTagInfo contentTagInfo);

    void importFromKeywordsFile(InputStream stream, long tagGroupId);

    void exportToKeywordsFile(OutputStream stream, long tagGroupId);
}
