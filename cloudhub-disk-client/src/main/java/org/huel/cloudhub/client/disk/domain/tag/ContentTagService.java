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
public interface ContentTagService {
    // TODO: refactor this interface

    // TODO: not use Pageable parameter in this method
    List<ContentTagInfo> getTags(Pageable pageable);

    List<TagGroupDto> getTagGroups(Pageable pageable);

    void createContentTagGroup(String name,
                               String description,
                               KeywordSearchScope searchScope);

    void createContentTag(String name,
                          String description,
                          List<TagKeyword> keywords);

    void importFromKeywordsFile(InputStream stream, long tagGroupId);

    void exportToKeywordsFile(OutputStream stream, long tagGroupId);
}
