package org.huel.cloudhub.client.disk.domain.tag;

import org.huel.cloudhub.client.disk.domain.tag.dto.ContentTagInfo;
import org.huel.cloudhub.client.disk.domain.tag.dto.TagGroupDto;
import org.huel.cloudhub.web.data.page.Pageable;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * @author RollW
 */
public interface ContentTagService extends ContentTagProvider {
    // TODO: refactor this interface

    ContentTagInfo getTagById(long id);

    TagGroupDto getTagGroupById(long id);

    List<ContentTagInfo> getTags(Pageable pageable);

    List<TagGroupDto> getTagGroups(Pageable pageable);

    ContentTagInfo getByName(String name);

    void createContentTagGroup(String name,
                               String description,
                               KeywordSearchScope searchScope);

    void createContentTag(String name,
                          String description,
                          List<TagKeyword> keywords);

    void importFromKeywordsFile(InputStream stream, long tagGroupId);

    void exportToKeywordsFile(OutputStream stream, long tagGroupId);
}
